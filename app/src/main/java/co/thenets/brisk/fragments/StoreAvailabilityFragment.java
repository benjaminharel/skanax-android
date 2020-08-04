package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.BecomeBriskerActivity;
import co.thenets.brisk.dialogs.NumberPickerDialogFragment;
import co.thenets.brisk.enums.Day;
import co.thenets.brisk.enums.FragmentType;
import co.thenets.brisk.enums.TimeDirection;
import co.thenets.brisk.events.FragmentResumedEvent;
import co.thenets.brisk.events.WorkingDayUpdatedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.interfaces.NavigationActivity;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.models.WorkingDay;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateOrUpdateStoreRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class StoreAvailabilityFragment extends BasicFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    private static final String LOG_TAG = StoreAvailabilityFragment.class.getSimpleName();

    private TextView mFromTimeTextViewSunday;
    private TextView mToTimeTextViewSunday;
    private CheckBox mCheckBoxSunday;
    private LinearLayout mHoursLayoutSunday;

    private TextView mFromTimeTextViewMonday;
    private TextView mToTimeTextViewMonday;
    private CheckBox mCheckBoxMonday;
    private LinearLayout mHoursLayoutMonday;

    private TextView mFromTimeTextViewTuesday;
    private TextView mToTimeTextViewTuesday;
    private CheckBox mCheckBoxTuesday;
    private LinearLayout mHoursLayoutTuesday;

    private TextView mFromTimeTextViewWednesday;
    private TextView mToTimeTextViewWednesday;
    private CheckBox mCheckBoxWednesday;
    private LinearLayout mHoursLayoutWednesday;

    private TextView mFromTimeTextViewThursday;
    private TextView mToTimeTextViewThursday;
    private CheckBox mCheckBoxThursday;
    private LinearLayout mHoursLayoutThursday;

    private TextView mFromTimeTextViewFriday;
    private TextView mToTimeTextViewFriday;
    private CheckBox mCheckBoxFriday;
    private LinearLayout mHoursLayoutFriday;

    private TextView mFromTimeTextViewSaturday;
    private TextView mToTimeTextViewSaturday;
    private CheckBox mCheckBoxSaturday;
    private LinearLayout mHoursLayoutSaturday;

    private String mGetFromHour;
    private String mGetToHour;
    private LinearLayout mProgressBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventsManager.getInstance().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventsManager.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_store_availability, container, false);
        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        if (getActivity() instanceof NavigationActivity)
        {
            menuInflater.inflate(R.menu.menu_with_done, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_done:
                updateStoreAvailability();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateStoreAvailability()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        Store storeForUpload = ContentManager.getInstance().getStoreForUpdateAvailability();
        RestClientManager.getInstance().updateStore(new CreateOrUpdateStoreRequest(storeForUpload), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                mProgressBarLayout.setVisibility(View.GONE);
                getActivity().onBackPressed();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mProgressBarLayout, getString(R.string.error_in_update_store_availability), Snackbar.LENGTH_LONG);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mProgressBarLayout, getString(R.string.error_in_update_store_availability), Snackbar.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getActivity() instanceof NavigationActivity)
        {
            getStoreFromServer();
        }

        EventsManager.getInstance().post(new FragmentResumedEvent(FragmentType.WORKING_HOURS));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof NavigationActivity)
        {
            ((NavigationActivity) getActivity()).setupNavigationDrawer(mToolbar);
        }
    }

    private void setViews()
    {
        setToolbar();
        setFab();
        setOtherViews();

    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.store_availability_toolbar);
        mToolbar.setTitle(getString(R.string.store_availability_fragment_title));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFab()
    {
        mFAB = (FloatingActionButton) mRootView.findViewById(R.id.store_availability_fab);
        if (getActivity() instanceof BecomeBriskerActivity)
        {
            mFAB.setVisibility(View.VISIBLE);
        }
    }

    private void setOtherViews()
    {
        mProgressBarLayout = (LinearLayout) mRootView.findViewById(R.id.progressBarLayout);

        // Sunday views
        mFromTimeTextViewSunday = (TextView) mRootView.findViewById(R.id.fromTimeTextViewSunday);
        mToTimeTextViewSunday = (TextView) mRootView.findViewById(R.id.toTimeTextViewSunday);
        mCheckBoxSunday = (CheckBox) mRootView.findViewById(R.id.checkBoxSunday);
        mHoursLayoutSunday = (LinearLayout) mRootView.findViewById(R.id.hoursLayoutSunday);
        mFromTimeTextViewSunday.setOnClickListener(this);
        mToTimeTextViewSunday.setOnClickListener(this);
        mCheckBoxSunday.setOnCheckedChangeListener(this);
        mCheckBoxSunday.setChecked(false);
        mHoursLayoutSunday.setVisibility(View.INVISIBLE);

        // Monday views
        mFromTimeTextViewMonday = (TextView) mRootView.findViewById(R.id.fromTimeTextViewMonday);
        mToTimeTextViewMonday = (TextView) mRootView.findViewById(R.id.toTimeTextViewMonday);
        mCheckBoxMonday = (CheckBox) mRootView.findViewById(R.id.checkBoxMonday);
        mHoursLayoutMonday = (LinearLayout) mRootView.findViewById(R.id.hoursLayoutMonday);
        mFromTimeTextViewMonday.setOnClickListener(this);
        mToTimeTextViewMonday.setOnClickListener(this);
        mCheckBoxMonday.setOnCheckedChangeListener(this);
        mCheckBoxMonday.setChecked(false);
        mHoursLayoutMonday.setVisibility(View.INVISIBLE);

        // Tuesday views
        mFromTimeTextViewTuesday = (TextView) mRootView.findViewById(R.id.fromTimeTextViewTuesday);
        mToTimeTextViewTuesday = (TextView) mRootView.findViewById(R.id.toTimeTextViewTuesday);
        mCheckBoxTuesday = (CheckBox) mRootView.findViewById(R.id.checkBoxTuesday);
        mHoursLayoutTuesday = (LinearLayout) mRootView.findViewById(R.id.hoursLayoutTuesday);
        mFromTimeTextViewTuesday.setOnClickListener(this);
        mToTimeTextViewTuesday.setOnClickListener(this);
        mCheckBoxTuesday.setOnCheckedChangeListener(this);
        mCheckBoxTuesday.setChecked(false);
        mHoursLayoutTuesday.setVisibility(View.INVISIBLE);

        // Wednesday views
        mFromTimeTextViewWednesday = (TextView) mRootView.findViewById(R.id.fromTimeTextViewWednesday);
        mToTimeTextViewWednesday = (TextView) mRootView.findViewById(R.id.toTimeTextViewWednesday);
        mCheckBoxWednesday = (CheckBox) mRootView.findViewById(R.id.checkBoxWednesday);
        mHoursLayoutWednesday = (LinearLayout) mRootView.findViewById(R.id.hoursLayoutWednesday);
        mFromTimeTextViewWednesday.setOnClickListener(this);
        mToTimeTextViewWednesday.setOnClickListener(this);
        mCheckBoxWednesday.setOnCheckedChangeListener(this);
        mCheckBoxWednesday.setChecked(false);
        mHoursLayoutWednesday.setVisibility(View.INVISIBLE);

        // Thursday views
        mFromTimeTextViewThursday = (TextView) mRootView.findViewById(R.id.fromTimeTextViewThursday);
        mToTimeTextViewThursday = (TextView) mRootView.findViewById(R.id.toTimeTextViewThursday);
        mCheckBoxThursday = (CheckBox) mRootView.findViewById(R.id.checkBoxThursday);
        mHoursLayoutThursday = (LinearLayout) mRootView.findViewById(R.id.hoursLayoutThursday);
        mFromTimeTextViewThursday.setOnClickListener(this);
        mToTimeTextViewThursday.setOnClickListener(this);
        mCheckBoxThursday.setOnCheckedChangeListener(this);
        mCheckBoxThursday.setChecked(false);
        mHoursLayoutThursday.setVisibility(View.INVISIBLE);

        // Friday views
        mFromTimeTextViewFriday = (TextView) mRootView.findViewById(R.id.fromTimeTextViewFriday);
        mToTimeTextViewFriday = (TextView) mRootView.findViewById(R.id.toTimeTextViewFriday);
        mCheckBoxFriday = (CheckBox) mRootView.findViewById(R.id.checkBoxFriday);
        mHoursLayoutFriday = (LinearLayout) mRootView.findViewById(R.id.hoursLayoutFriday);
        mFromTimeTextViewFriday.setOnClickListener(this);
        mToTimeTextViewFriday.setOnClickListener(this);
        mCheckBoxFriday.setOnCheckedChangeListener(this);
        mCheckBoxFriday.setChecked(false);
        mHoursLayoutFriday.setVisibility(View.INVISIBLE);

        // Saturday views
        mFromTimeTextViewSaturday = (TextView) mRootView.findViewById(R.id.fromTimeTextViewSaturday);
        mToTimeTextViewSaturday = (TextView) mRootView.findViewById(R.id.toTimeTextViewSaturday);
        mCheckBoxSaturday = (CheckBox) mRootView.findViewById(R.id.checkBoxSaturday);
        mHoursLayoutSaturday = (LinearLayout) mRootView.findViewById(R.id.hoursLayoutSaturday);
        mFromTimeTextViewSaturday.setOnClickListener(this);
        mToTimeTextViewSaturday.setOnClickListener(this);
        mCheckBoxSaturday.setOnCheckedChangeListener(this);
        mCheckBoxSaturday.setChecked(false);
        mHoursLayoutSaturday.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v)
    {
        NumberPickerDialogFragment numberPickerDialogFragment = new NumberPickerDialogFragment();
        Bundle bundle = new Bundle();

        switch (v.getId())
        {
            case R.id.fromTimeTextViewSunday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.SUNDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.FROM);
                break;
            case R.id.toTimeTextViewSunday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.SUNDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.TO);
                break;
            case R.id.fromTimeTextViewMonday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.MONDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.FROM);
                break;
            case R.id.toTimeTextViewMonday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.MONDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.TO);
                break;
            case R.id.fromTimeTextViewTuesday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.TUESDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.FROM);
                break;
            case R.id.toTimeTextViewTuesday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.TUESDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.TO);
                break;
            case R.id.fromTimeTextViewWednesday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.WEDNESDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.FROM);
                break;
            case R.id.toTimeTextViewWednesday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.WEDNESDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.TO);
                break;
            case R.id.fromTimeTextViewThursday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.THURSDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.FROM);
                break;
            case R.id.toTimeTextViewThursday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.THURSDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.TO);
                break;
            case R.id.fromTimeTextViewFriday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.FRIDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.FROM);
                break;
            case R.id.toTimeTextViewFriday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.FRIDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.TO);
                break;
            case R.id.fromTimeTextViewSaturday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.SATURDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.FROM);
                break;
            case R.id.toTimeTextViewSaturday:
                bundle.putSerializable(Params.SELECTED_DAY, Day.SATURDAY);
                bundle.putSerializable(Params.FROM_OR_TO, TimeDirection.TO);
                break;
        }
        numberPickerDialogFragment.setArguments(bundle);
        numberPickerDialogFragment.show(getFragmentManager(), Params.TIME_PICKER);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.checkBoxSunday:
                if (isChecked)
                {
                    setWorkingDayDataValues(Day.SUNDAY);
                    mHoursLayoutSunday.setVisibility(View.VISIBLE);
                }
                else
                {
                    mHoursLayoutSunday.setVisibility(View.INVISIBLE);
                    ContentManager.getInstance().removeWorkingDay(Day.SUNDAY);
                }
                break;
            case R.id.checkBoxMonday:
                if (isChecked)
                {
                    setWorkingDayDataValues(Day.MONDAY);
                    mHoursLayoutMonday.setVisibility(View.VISIBLE);
                }
                else
                {
                    mHoursLayoutMonday.setVisibility(View.INVISIBLE);
                    ContentManager.getInstance().removeWorkingDay(Day.MONDAY);
                }
                break;
            case R.id.checkBoxTuesday:
                if (isChecked)
                {
                    setWorkingDayDataValues(Day.TUESDAY);
                    mHoursLayoutTuesday.setVisibility(View.VISIBLE);
                }
                else
                {
                    mHoursLayoutTuesday.setVisibility(View.INVISIBLE);
                    ContentManager.getInstance().removeWorkingDay(Day.TUESDAY);
                }
                break;
            case R.id.checkBoxWednesday:
                if (isChecked)
                {
                    setWorkingDayDataValues(Day.WEDNESDAY);
                    mHoursLayoutWednesday.setVisibility(View.VISIBLE);
                }
                else
                {
                    ContentManager.getInstance().removeWorkingDay(Day.WEDNESDAY);
                    mHoursLayoutWednesday.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.checkBoxThursday:
                if (isChecked)
                {
                    setWorkingDayDataValues(Day.THURSDAY);
                    mHoursLayoutThursday.setVisibility(View.VISIBLE);
                }
                else
                {
                    ContentManager.getInstance().removeWorkingDay(Day.THURSDAY);
                    mHoursLayoutThursday.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.checkBoxFriday:
                if (isChecked)
                {
                    setWorkingDayDataValues(Day.FRIDAY);
                    mHoursLayoutFriday.setVisibility(View.VISIBLE);
                }
                else
                {
                    ContentManager.getInstance().removeWorkingDay(Day.FRIDAY);
                    mHoursLayoutFriday.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.checkBoxSaturday:
                if (isChecked)
                {
                    setWorkingDayDataValues(Day.SATURDAY);
                    mHoursLayoutSaturday.setVisibility(View.VISIBLE);
                }
                else
                {
                    ContentManager.getInstance().removeWorkingDay(Day.SATURDAY);
                    mHoursLayoutSaturday.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void setWorkingDayDataValues(Day selectedDay)
    {
        WorkingDay workingDay = ContentManager.getInstance().getWorkingDay(selectedDay);
        if(workingDay != null)
        {
            // workingDay exists, so just load the values from the model to UI
            switch (selectedDay)
            {
                case SUNDAY:
                    mFromTimeTextViewSunday.setText(formattedWorkingTime(workingDay.getFrom().getHour()));
                    mToTimeTextViewSunday.setText(formattedWorkingTime(workingDay.getTo().getHour()));
                    break;
                case MONDAY:
                    mFromTimeTextViewMonday.setText(formattedWorkingTime(workingDay.getFrom().getHour()));
                    mToTimeTextViewMonday.setText(formattedWorkingTime(workingDay.getTo().getHour()));
                    break;
                case TUESDAY:
                    mFromTimeTextViewTuesday.setText(formattedWorkingTime(workingDay.getFrom().getHour()));
                    mToTimeTextViewTuesday.setText(formattedWorkingTime(workingDay.getTo().getHour()));
                    break;
                case WEDNESDAY:
                    mFromTimeTextViewWednesday.setText(formattedWorkingTime(workingDay.getFrom().getHour()));
                    mToTimeTextViewWednesday.setText(formattedWorkingTime(workingDay.getTo().getHour()));
                    break;
                case THURSDAY:
                    mFromTimeTextViewThursday.setText(formattedWorkingTime(workingDay.getFrom().getHour()));
                    mToTimeTextViewThursday.setText(formattedWorkingTime(workingDay.getTo().getHour()));
                    break;
                case FRIDAY:
                    mFromTimeTextViewFriday.setText(formattedWorkingTime(workingDay.getFrom().getHour()));
                    mToTimeTextViewFriday.setText(formattedWorkingTime(workingDay.getTo().getHour()));
                    break;
                case SATURDAY:
                    mFromTimeTextViewSaturday.setText(formattedWorkingTime(workingDay.getFrom().getHour()));
                    mToTimeTextViewSaturday.setText(formattedWorkingTime(workingDay.getTo().getHour()));
                    break;
            }
        }
        else
        {
            // workingDay not exists, load the default values to the model
            ContentManager.getInstance().addWorkingDay(selectedDay, Params.DEFAULT_AVAILABILITY_START_HOUR, Params.DEFAULT_AVAILABILITY_END_HOUR);

            // update UI
            switch (selectedDay)
            {
                case SUNDAY:
                    break;
                case MONDAY:
                    mFromTimeTextViewMonday.setText(formattedWorkingTime(Params.DEFAULT_AVAILABILITY_START_HOUR));
                    mToTimeTextViewMonday.setText(formattedWorkingTime(Params.DEFAULT_AVAILABILITY_END_HOUR));
                    break;
                case TUESDAY:
                    break;
                case WEDNESDAY:
                    break;
                case THURSDAY:
                    break;
                case FRIDAY:
                    break;
                case SATURDAY:
                    break;
            }
        }
    }

    public void getStoreFromServer()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        RestClientManager.getInstance().getStore(new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                setWorkingDays();
                mProgressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setWorkingDays()
    {
        ArrayList<WorkingDay> workingDays = ContentManager.getInstance().getWorkingDays();
        for (WorkingDay workingDay  : workingDays)
        {
            switch (Day.getDayType(workingDay.getDayLabel()))
            {
                case SUNDAY:
                    mCheckBoxSunday.setChecked(true);
                    break;
                case MONDAY:
                    mCheckBoxMonday.setChecked(true);
                    break;
                case TUESDAY:
                    mCheckBoxTuesday.setChecked(true);
                    break;
                case WEDNESDAY:
                    mCheckBoxWednesday.setChecked(true);
                    break;
                case THURSDAY:
                    mCheckBoxThursday.setChecked(true);
                    break;
                case FRIDAY:
                    mCheckBoxFriday.setChecked(true);
                    break;
                case SATURDAY:
                    mCheckBoxSaturday.setChecked(true);
                    break;
            }
        }
    }

    private String formattedWorkingTime(int value)
    {
        String formattedValue = "";
        if (value < 10)
        {
            formattedValue = "0" + String.valueOf(value) + ":00";
        }
        else
        {
            formattedValue = String.valueOf(value) + ":00";
        }

        return formattedValue;
    }

    @Subscribe
    public void onWorkingDayUpdated(WorkingDayUpdatedEvent workingDayUpdatedEvent)
    {
        setWorkingDayDataValues(workingDayUpdatedEvent.getUpdatedDay());
    }
}
