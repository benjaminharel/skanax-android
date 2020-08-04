package co.thenets.brisk.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.BecomeBriskerActivity;
import co.thenets.brisk.custom.CircleMapView;
import co.thenets.brisk.enums.FragmentType;
import co.thenets.brisk.events.FragmentResumedEvent;
import co.thenets.brisk.events.LocationPermissionDenialEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.interfaces.NavigationActivity;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.GeoArea;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateOrUpdateStoreRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public class StoreGeoDataFragment extends BasicFragment
        implements OnMapReadyCallback,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMyLocationChangeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener
{
    private static final String LOG_TAG = StoreGeoDataFragment.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private AppCompatActivity mAppCompatActivity;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private CircleMapView mCircleView;
    private int mRadius;
    private int mViewWidthHalf;
    private int mViewHeightHalf;
    private float mDistanceInMetersTwoPoint;

    private boolean mCheckOnClick = false;
    private TextView mSelectRadiusTextView;
    private float mCameraZoom;
    private LatLng mLatLngCenter;
    private Boolean mIsPositionRadiusFill = false;
    private DrawerLayout mDrawerLayout;
    private float mCurrentZoomMap;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EventsManager.getInstance().post(new FragmentResumedEvent(FragmentType.WORKING_RADIUS));
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        mRootView = inflater.inflate(R.layout.fragment_store_geo_data, container, false);
//        return mRootView;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (mRootView != null)
        {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
            {
                parent.removeView(mRootView);
            }
        }
        try
        {
            mRootView = inflater.inflate(R.layout.fragment_store_geo_data, container, false);
        }
        catch (InflateException e)
        {
        /* map is already there, just return view as it is */
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
        askForLocationPermissions();
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
                if (!(getActivity() instanceof BecomeBriskerActivity))
                {
                    updateServerWithGeoData();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateServerWithGeoData()
    {
        if (mCameraZoom < 12)
        {
            UIManager.getInstance().displaySnackBarError(getView(), getString(R.string.empty_store_geo_location), Snackbar.LENGTH_LONG);
        }
        else
        {
            Store storeForUpload;

            if (ContentManager.getInstance().getStore() == null)
            {
                ContentManager.getInstance().setStoreGeoArea(new GeoArea(mLatLngCenter.latitude, mLatLngCenter.longitude, Math.round(mDistanceInMetersTwoPoint)));
                storeForUpload = new Store();
                storeForUpload.setIsActive(true);
                storeForUpload.setGeoArea(ContentManager.getInstance().getStoreGeoArea());
            }
            else
            {
                storeForUpload = ContentManager.getInstance().getStore();
                storeForUpload.setGeoArea(ContentManager.getInstance().getStoreGeoArea());
            }

            RestClientManager.getInstance().updateStore(new CreateOrUpdateStoreRequest(storeForUpload), new RequestListener()
            {
                @Override
                public void onSuccess()
                {
                    Toast.makeText(getActivity(), getString(R.string.store_area_updated), Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }

                @Override
                public void onInternalServerFailure(ErrorResponse error)
                {
                    UIManager.getInstance().displaySnackBarError(mToolbar, getString(R.string.error_in_update_store_details), Snackbar.LENGTH_LONG);
                }

                @Override
                public void onNetworkFailure(RetrofitError error)
                {
                    UIManager.getInstance().displaySnackBarError(mToolbar, getString(R.string.error_in_update_store_details), Snackbar.LENGTH_LONG);
                }
            });
        }
    }


    private void askForLocationPermissions()
    {
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED && hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        setMapsViews();
    }

    private void setMapsViews()
    {
        setMapViews();
        setGoogleApi();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted!
                    setMapsViews();
                }
                else
                {
                    // permission denied! Disable the functionality that depends on this permission.
                    EventsManager.getInstance().post(new LocationPermissionDenialEvent());
                    getActivity().finish();
                }
                return;
            }
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity)
        {
            activity = (Activity) context;
            mAppCompatActivity = (AppCompatActivity) activity;
        }
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
        setDrawerLayout();
        setOtherViews();
        setFab();
    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.store_geo_data_toolbar);
        mToolbar.setTitle(getString(R.string.store_geo_data_fragment_title));
        mAppCompatActivity.setSupportActionBar(mToolbar);
        if (getActivity() instanceof BecomeBriskerActivity)
        {
            mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setDrawerLayout()
    {
        mDrawerLayout = (DrawerLayout) mRootView.findViewById(R.id.drawer_layout);
    }

    private void setFab()
    {
        mFAB = (FloatingActionButton) mRootView.findViewById(R.id.store_geo_data_fab);
        if (getActivity() instanceof BecomeBriskerActivity)
        {
            mFAB.setVisibility(View.VISIBLE);
        }
    }

    private void setOtherViews()
    {
        mSelectRadiusTextView = (TextView) mRootView.findViewById(R.id.selectRadiusTextView);
        mCircleView = (CircleMapView) mRootView.findViewById(R.id.circleView);
    }

    private void setMapViews()
    {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        supportMapFragment.getMapAsync(this);
    }

    private void setGoogleApi()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        LatLng latLng;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (currentLocation != null)
        {
            if (ContentManager.getInstance().getStore() != null)
            {
                GeoArea savedStoreGeoArea = ContentManager.getInstance().getStore().getGeoArea();
                if (savedStoreGeoArea == null)
                {
                    latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                }
                else
                {
                    latLng = new LatLng(savedStoreGeoArea.getLatitude(), savedStoreGeoArea.getLongitude());
                }
            }
            else
            {
                latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            }

            float sellerMapZoom = ContentManager.getInstance().getSellerMapZoom();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, sellerMapZoom);
            mMap.animateCamera(cameraUpdate);

            mCheckOnClick = true;
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
        mCameraZoom = cameraPosition.zoom;
        mSelectRadiusTextView.setVisibility(View.VISIBLE);
        if (cameraPosition.zoom != Params.DEFAULT_SELLER_MAP_ZOOM)
        {
            mSelectRadiusTextView.setVisibility(View.INVISIBLE);
            mIsPositionRadiusFill = false;
        }

        if (mCheckOnClick)
        {
            logicUi();
            mIsPositionRadiusFill = true;
        }
        mCircleView.setLabelText(String.valueOf((int) mDistanceInMetersTwoPoint) + "m");

        if (cameraPosition.zoom < 11.35)
        {
            Log.i(LOG_TAG, cameraPosition.zoom + "zoom");
            mCircleView.setVisibility(View.INVISIBLE);
            mSelectRadiusTextView.setVisibility(View.VISIBLE);
            mIsPositionRadiusFill = false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mCheckOnClick = true;
        mIsPositionRadiusFill = true;
        logicUi();
        mCircleView.setLabelText("");
        setUiSettingMap();
    }

    private void setUiSettingMap()
    {
        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setCompassEnabled(true);
        mapSettings.setTiltGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnCameraChangeListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        return false;
    }

    @Override
    public void onMyLocationChange(Location location)
    {
        LatLng mCurrentLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLocationLatLng, 15);
        mMap.animateCamera(cameraUpdate);
        logicUi();
        mCircleView.setLabelText(String.valueOf((int) mDistanceInMetersTwoPoint) + "m");
        mIsPositionRadiusFill = true;
    }

    private void setRadiusArea()
    {
        Projection projection = mMap.getProjection();
        mLatLngCenter = projection.fromScreenLocation(new Point(mViewWidthHalf, mViewHeightHalf));

        Log.d(LOG_TAG, String.valueOf(mLatLngCenter.latitude + "," + mLatLngCenter.longitude));

        Location centerLocation = new Location("");
        centerLocation.setLatitude(mLatLngCenter.latitude);
        centerLocation.setLongitude(mLatLngCenter.longitude);
        LatLng latLngRadius = projection.fromScreenLocation(new Point(mViewHeightHalf + mRadius, mViewWidthHalf + mRadius));

        Location radiusLocation = new Location("");
        radiusLocation.setLatitude(latLngRadius.latitude);
        radiusLocation.setLongitude(latLngRadius.longitude);

        mDistanceInMetersTwoPoint = centerLocation.distanceTo(radiusLocation);

        if (mCameraZoom > 12)
        {
            mCurrentZoomMap = mMap.getCameraPosition().zoom;
            ContentManager.getInstance().setSellerMapZoom(mCurrentZoomMap);
            ContentManager.getInstance().setStoreGeoArea(new GeoArea(mLatLngCenter.latitude, mLatLngCenter.longitude, Math.round(mDistanceInMetersTwoPoint)));
        }
    }

    private void onDrawCircle()
    {
        mCircleView.setVisibility(View.VISIBLE);
        mCircleView.setCircleColor(Color.parseColor(getString(R.string.circle_border_color)));
        mCircleView.setLabelColor(Color.parseColor(getString(R.string.circle_border_color)));
        if (mRadius == 0)
        {
            mRadius = mCircleView.getRadius();
        }

        mViewWidthHalf = mCircleView.getMeasuredWidth() / 2;
        mViewHeightHalf = mCircleView.getMeasuredHeight() / 2;
    }

    private void logicUi()
    {
        onDrawCircle();
        setRadiusArea();
    }

    public boolean isGeoDataReady()
    {
        if (mIsPositionRadiusFill)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
