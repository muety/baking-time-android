package com.github.n1try.bakingtime.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.model.RecipeStep;
import com.github.n1try.bakingtime.utils.BasicUtils;
import com.github.n1try.bakingtime.utils.Constants;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StepDetailFragment extends Fragment implements Player.EventListener {
    @BindView(R.id.next_step_fab)
    FloatingActionButton nextFab;
    @BindView(R.id.prev_step_fab)
    FloatingActionButton prevFab;
    @BindView(R.id.step_instructions_title_tv)
    TextView stepInstructionsTitle;
    @BindView(R.id.step_instructions_tv)
    TextView stepInstructions;
    @BindView(R.id.step_player)
    PlayerView stepPlayerView;
    @BindView(R.id.step_thumbnail_iv)
    ImageView thumbnailView;

    private Recipe mRecipe;
    private RecipeStep mStep;
    private int mStepIndex;
    private OnRecipeStepChangeListener mOnRecipeStepChangeListener;
    private boolean isTablet;
    private ExoPlayer mPlayer;
    private Handler mHandler;
    private long mPlayerStartPosition = 0;
    private boolean mPlayerPlayWhenReady = true;

    public static StepDetailFragment newInstance(Recipe recipe, int stepIndex) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_RECIPE, recipe);
        bundle.putInt(Constants.KEY_RECIPE_STEP_INDEX, stepIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe = getArguments().getParcelable(Constants.KEY_RECIPE);
        mStepIndex = getArguments().getInt(Constants.KEY_RECIPE_STEP_INDEX);
        mStep = mRecipe.getSteps().get(mStepIndex);
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        StringBuilder builder = new StringBuilder(mRecipe.getName())
                .append(" - ").append(getResources().getString(R.string.step)).append(" ")
                .append(mStepIndex + 1);
        getActivity().setTitle(BasicUtils.styleTitle(builder.toString()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);

        RecipeStep step = mRecipe.getSteps().get(mStepIndex);
        stepInstructions.setText(step.getDescription());
        stepInstructionsTitle.setText(step.getShortDescription());

        if (mStepIndex == 0 || isTablet) prevFab.setVisibility(View.GONE);
        if (mStepIndex == mRecipe.getSteps().size() - 1 || isTablet) nextFab.setVisibility(View.GONE);

        if (savedInstanceState != null) {
            mPlayerStartPosition = savedInstanceState.getLong(Constants.KEY_PLAYER_POSITION, mPlayerStartPosition);
            mPlayerPlayWhenReady = savedInstanceState.getBoolean(Constants.KEY_PLAYER_PLAY_STATE, mPlayerPlayWhenReady);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mOnRecipeStepChangeListener = (OnRecipeStepChangeListener) getActivity();
        } catch (ClassCastException e) {
            Log.w(getTag(), "Could not bind OnRecipeStepChangeListener to fragment");
        }
    }

    @Override
    public void onResume() {
        mPlayer = null;
        if (!TextUtils.isEmpty(mStep.getVideoUrl())) {
            initPlayer(Uri.parse(mStep.getVideoUrl()));
        } else {
            showThumbnail();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mPlayer != null) {
            mPlayerPlayWhenReady = mPlayer.getPlayWhenReady();
            mPlayerStartPosition = mPlayer.getCurrentPosition();
        }
        releasePlayer();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            outState.putLong(Constants.KEY_PLAYER_POSITION, mPlayer.getCurrentPosition());
            outState.putBoolean(Constants.KEY_PLAYER_PLAY_STATE, mPlayer.getPlayWhenReady());
        }
    }

    @OnClick(R.id.next_step_fab)
    void nextStep() {
        mOnRecipeStepChangeListener.onNextStep(mStepIndex);
    }

    @OnClick(R.id.prev_step_fab)
    void prevStep() {
        mOnRecipeStepChangeListener.onPreviousStep(mStepIndex);
    }

    private void initPlayer(Uri mediaUri) {
        if (mPlayer == null) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                    getContext(),
                    Util.getUserAgent(
                            getContext(),
                            BasicUtils.getApplicationName(getContext())
                    )
            );
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
            mPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
            stepPlayerView.setPlayer(mPlayer);

            mPlayer.addListener(this);

            ExtractorMediaSource.Factory mediaSourceFactory = new ExtractorMediaSource.Factory(dataSourceFactory);
            MediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaUri);
            mPlayer.prepare(mediaSource);
            mPlayer.seekTo(mPlayerStartPosition);
            mPlayer.setPlayWhenReady(mPlayerPlayWhenReady);
        }
    }

    private void releasePlayer() {
        if (mPlayer == null) return;
        mPlayer.stop();
        mPlayer.release();
    }

    private void showPlayer() {
        thumbnailView.setVisibility(View.GONE);
        stepPlayerView.setVisibility(View.VISIBLE);
    }

    private boolean isPlayerShown() {
        return stepPlayerView.getVisibility() == View.VISIBLE;
    }

    private void showThumbnail() {
        stepPlayerView.setVisibility(View.GONE);
        thumbnailView.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mStep.getThumbnailUrl())) {
            Picasso.with(getContext()).load(mStep.getThumbnailUrl()).into(thumbnailView);
            thumbnailView.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mHandler == null) {
            mHandler = new Handler();
            Runnable checkBuffering = () -> {
                if (mPlayer == null) return;
                if (mPlayer.getBufferedPercentage() < 10) {
                    releasePlayer();
                    showThumbnail();
                }
            };
            mHandler.postDelayed(checkBuffering, Math.round(Constants.VIDEO_MAX_LOADING_TIME_SECS * 1000));
        }
        if (!isPlayerShown() && playWhenReady) showPlayer();
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.w(getTag(), error.getMessage());
        releasePlayer();
        showThumbnail();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {

    }

    interface OnRecipeStepChangeListener {
        void onNextStep(int currentStepIndex);

        void onPreviousStep(int currentStepIndex);
    }
}
