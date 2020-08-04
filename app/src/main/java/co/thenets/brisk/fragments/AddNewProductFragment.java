package co.thenets.brisk.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.BarcodeActivity;
import co.thenets.brisk.activities.BecomeBriskerActivity;
import co.thenets.brisk.adapters.AddProductRecyclerAdapter;
import co.thenets.brisk.custom.DividerItemDecoration;
import co.thenets.brisk.custom.LinearLayoutManager;
import co.thenets.brisk.dialogs.PickImageDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.SourceImageType;
import co.thenets.brisk.events.ImageCroppedEvent;
import co.thenets.brisk.events.SwitchStateEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.NavigationActivity;
import co.thenets.brisk.interfaces.OnProductSelectedListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Category;
import co.thenets.brisk.models.Product;
import co.thenets.brisk.models.StoreProduct;
import co.thenets.brisk.models.StoreProductForUpload;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.AdvancedConfiguration;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.responses.GetProductsResponse;
import co.thenets.brisk.rest.service.RequestListener;
import co.thenets.brisk.rest.service.RequestProductsListener;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddNewProductFragment extends BasicFragment implements View.OnClickListener
{
    private static final String LOG_TAG = AddNewProductFragment.class.getSimpleName();
    private static final String IMAGE_ADDED = "image_added";
    private View mRootView;

    private RecyclerView mRecyclerView;
    private EditText mProductNameEditText;
    private ImageView mProductImageView;

    private EditText mBrandEditText, mPriceEditText, mNotesEditText;
    private Spinner mCategoriesSpinner;
    private Spinner mSubCategoriesSpinner;

    private StoreProductForUpload mStoreProductForUpload;
    private boolean mIsSubCategorySpinnerNeedsAutoSelection;
    private Category mSelectedCategory;
    private int mSelectedSubCategoryCode;
    private String mSelectedSubCategoryName;
    private boolean mIsInAutoSelectionMode = true;
    private Product mSelectedProduct;
    private Button mAddProductButton;
    private LinearLayout mProgressBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventsManager.getInstance().register(this);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        EventsManager.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_add_new_product, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
        initCategoriesSpinners();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        if (getActivity() instanceof NavigationActivity)
        {
            menuInflater.inflate(R.menu.menu_add_inventory, menu);
        }
        else
        {
            menuInflater.inflate(R.menu.menu_set_inventory, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_done:
                if (getActivity() instanceof BecomeBriskerActivity)
                {
                    getActivity().finish();
                    EventsManager.getInstance().post(new SwitchStateEvent());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCategoriesSpinners()
    {
        if(ContentManager.getInstance().getCategoryList().isEmpty())
        {
            mProgressBarLayout.setVisibility(View.VISIBLE);
            RestClientManager.getInstance().getCategories(new RequestListener()
            {
                @Override
                public void onSuccess()
                {
                    mProgressBarLayout.setVisibility(View.GONE);
                    loadSpinners();
                }

                @Override
                public void onInternalServerFailure(ErrorResponse error)
                {
                    mProgressBarLayout.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.error_in_getting_categories_from_server), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }

                @Override
                public void onNetworkFailure(RetrofitError error)
                {
                    mProgressBarLayout.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.error_in_getting_categories_from_server), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            });
        }
        else
        {
            loadSpinners();
        }
    }

    private void loadSpinners()
    {
        mSubCategoriesSpinner.setEnabled(false);
        final ArrayList<String> categoriesArray = new ArrayList<>();
        final ArrayList<String> subCategoriesArray = new ArrayList<>();
        categoriesArray.add(getString(R.string.select_category));

        for (Category category : ContentManager.getInstance().getCategoryList())
        {
            categoriesArray.add(category.getName());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoriesArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoriesSpinner.setAdapter(spinnerArrayAdapter);
        mCategoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position > 0)
                {
                    int categoryPosition = position - 1;
                    ArrayList<Category> categoryList = ContentManager.getInstance().getCategoryList();
                    Category category = categoryList.get(categoryPosition);
                    subCategoriesArray.clear();
                    subCategoriesArray.add(getString(R.string.select_subcategory));

                    for (Category subcategory : category.getSubCategories())
                    {
                        subCategoriesArray.add(subcategory.getName());
                    }

                    ArrayAdapter<String> subCategoriesSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, subCategoriesArray);
                    subCategoriesSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSubCategoriesSpinner.setAdapter(subCategoriesSpinnerArrayAdapter);
                    mSubCategoriesSpinner.setEnabled(true);
                }
                else if (position == 0)
                {
                    subCategoriesArray.clear();
                    subCategoriesArray.add(getString(R.string.select_subcategory));
                    mSubCategoriesSpinner.setEnabled(false);
                }

                setSubCategorySpinnerIfNeeded();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setViews()
    {
        setToolbar();
        setRecyclerView();
        setSearchProductComponent();
        setOtherViews();
        setFab();
    }

    private void setFab()
    {
        FloatingActionButton mAddProductWithBarcodeFab = (FloatingActionButton) mRootView.findViewById(R.id.addProductWithBarcodeFab);
        if(mAddProductWithBarcodeFab != null)
        {
            mAddProductWithBarcodeFab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.add_items_for_sale));
        if (getActivity() instanceof AppCompatActivity)
        {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
    }

    private void setRecyclerView()
    {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.LIST_VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setVisibility(View.GONE);
    }

    private void setOtherViews()
    {
        ScrollView scrollView = (ScrollView) mRootView.findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    // Hide suggested RecyclerView if the user press outside the suggestions area
                    mRecyclerView.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mCategoriesSpinner = (Spinner) mRootView.findViewById(R.id.categorySpinner);
        mSubCategoriesSpinner = (Spinner) mRootView.findViewById(R.id.subCategorySpinner);

        mProgressBarLayout = (LinearLayout) mRootView.findViewById(R.id.progressBarLayout);
        mProgressBarLayout.setOnClickListener(this);

        mAddProductButton = (Button) mRootView.findViewById(R.id.addProductButton);
        mAddProductButton.setOnClickListener(this);

        mNotesEditText = (EditText) mRootView.findViewById(R.id.notesEditText);
        mPriceEditText = (EditText) mRootView.findViewById(R.id.priceEditText);
        mBrandEditText = (EditText) mRootView.findViewById(R.id.brandEditText);

        mProductImageView = (ImageView) mRootView.findViewById(R.id.productImage);
        mProductImageView.setOnClickListener(this);

        mNotesEditText.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    addProduct();
                }
                return false;
            }
        });
    }

    private void setSearchProductComponent()
    {
        mProductNameEditText = (EditText) mRootView.findViewById(R.id.productAutocomplete);

        mProductNameEditText.addTextChangedListener
                (new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        if (mSelectedProduct != null && !mSelectedProduct.getName().equals(mProductNameEditText.getText().toString()))
                        {
                            // The user changed his selection, so show him auto select again
                            mIsInAutoSelectionMode = true;
                        }

                        if (mIsInAutoSelectionMode)
                        {
                            getProducts(s.toString());
                        }

                        // Clear last selection if needed
                        if(TextUtils.isEmpty(s.toString()))
                        {
                            clearFields();
                        }
                    }
                });

        mProductNameEditText.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    addProduct();
                }
                return false;
            }
        });
    }

    private void clearFields()
    {
        mIsInAutoSelectionMode = true;
        mCategoriesSpinner.setSelection(0);
        mSubCategoriesSpinner.setSelection(0);
        mSubCategoriesSpinner.setEnabled(false);
        mBrandEditText.setText("");
        mPriceEditText.setText("");
        mNotesEditText.setText("");
    }

    private void getProducts(String value)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put(AdvancedConfiguration.QUERY_KEY, value);
        params.put(AdvancedConfiguration.LIMIT_KEY, String.valueOf(Params.ADD_NEW_PRODUCT_SUGGESTION_LIMIT));

        RestClientManager.getInstance().getProducts(params, new RequestProductsListener()
        {
            @Override
            public void onSuccess(GetProductsResponse getProductsResponse)
            {
                ArrayList<Product> productList = getProductsResponse.getProductList();
                if (productList.size() > 0 && !TextUtils.isEmpty(mProductNameEditText.getText().toString()))
                {
                    AddProductRecyclerAdapter productRecyclerAdapter = new AddProductRecyclerAdapter(getActivity(), productList, new OnProductSelectedListener()
                    {
                        @Override
                        public void onProductSelected(Product product)
                        {
                            mSelectedProduct = product;
                            mIsInAutoSelectionMode = false;
                            mProductNameEditText.setText(product.getName());
                            mBrandEditText.setText(product.getBrand());
                            mPriceEditText.setText(String.valueOf(product.getMarketPrice()));
                            UIManager.getInstance().loadImage(getActivity(), product.getPhotoGallery().getMedium(), mProductImageView, ImageType.PRODUCT);
                            mProductImageView.setTag(IMAGE_ADDED);
                            setCategorySpinner(product.getCategoryCode(), product.getSubCategoryCode());
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    });
                    mRecyclerView.setAdapter(productRecyclerAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                else
                {
                    mRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchWithoutValue()
            {
                mRecyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {

            }
        });
    }

    private void setCategorySpinner(int selectedCategoryCode, int selectedSubCategoryCode)
    {
        mSelectedCategory = null;
        int selectedCategoryPosition = 0;

        // find the selected category position in categories list
        ArrayList<Category> categoryList = ContentManager.getInstance().getCategoryList();
        for (int i = 0; i < categoryList.size(); i++)
        {
            if (categoryList.get(i).getCode() == selectedCategoryCode)
            {
                mSelectedCategory = categoryList.get(i);
                // +1 for the header of the spinner
                selectedCategoryPosition = i + 1;
                break;
            }
        }
        // set selected position in categories spinner
        mCategoriesSpinner.setSelection(selectedCategoryPosition);
        mIsSubCategorySpinnerNeedsAutoSelection = true;
        mSelectedSubCategoryCode = selectedSubCategoryCode;
    }

    private void setSubCategorySpinnerIfNeeded()
    {
        int selectedSubCategoryPosition = 0;
        if (mIsSubCategorySpinnerNeedsAutoSelection)
        {
            // find the selected sub category position in categories list
            ArrayList<Category> subCategoryList = mSelectedCategory.getSubCategories();
            for (int i = 0; i < subCategoryList.size(); i++)
            {
                if (subCategoryList.get(i).getCode() == mSelectedSubCategoryCode)
                {
                    // +1 for the header of the spinner
                    selectedSubCategoryPosition = i + 1;

                    // save selected subCategory name for later uses
                    mSelectedSubCategoryName = subCategoryList.get(i).getName();
                    break;
                }
            }
            // set selected position in sub categories spinner
            mSubCategoriesSpinner.setSelection(selectedSubCategoryPosition);
        }

        mIsSubCategorySpinnerNeedsAutoSelection = false;
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addProductButton:
                addProduct();
                break;
            case R.id.productImage:
                PickImageDialog pickImageDialog = PickImageDialog.newInstance(SourceImageType.FROM_ADD_NEW_PRODUCT);
                pickImageDialog.show(getFragmentManager(), PickImageDialog.class.getSimpleName());
                break;
        }
    }

    private void addProduct()
    {
        if (areFieldsReady())
        {
            if (isNewProduct())
            {
                addNewProduct();
            }
            else
            {
                addExistingProduct();
            }
        }
    }

    private boolean areFieldsReady()
    {
        UIManager.getInstance().hideKeyboard(mAddProductButton, getActivity());

        if (TextUtils.isEmpty(mProductNameEditText.getText().toString()))
        {
            UIManager.getInstance().displaySnackBarError(mAddProductButton, getString(R.string.missing_product_name_error), Snackbar.LENGTH_LONG);
            return false;
        }
        else if(mCategoriesSpinner.getSelectedItemPosition() <= 0)
        {
            UIManager.getInstance().displaySnackBarError(mAddProductButton, getString(R.string.missing_category_error), Snackbar.LENGTH_LONG);
            return false;
        }
        else if(mSubCategoriesSpinner.getSelectedItemPosition() <= 0)
        {
            UIManager.getInstance().displaySnackBarError(mAddProductButton, getString(R.string.missing_sub_category_error), Snackbar.LENGTH_LONG);
            return false;
        }
        else if(TextUtils.isEmpty(mPriceEditText.getText().toString()))
        {
            UIManager.getInstance().displaySnackBarError(mAddProductButton, getString(R.string.missing_product_price_error), Snackbar.LENGTH_LONG);
            return false;
        }
        else if (!(mProductImageView.getTag() != null && mProductImageView.getTag().equals(IMAGE_ADDED)))
        {
            UIManager.getInstance().displaySnackBarError(mAddProductButton, getString(R.string.missing_product_image_error), Snackbar.LENGTH_LONG);
            return false;
        }
        return true;
    }

    private boolean isNewProduct()
    {
        try
        {
            if (mSelectedProduct != null)
            {
                if (mSelectedProduct.getName().equals(mProductNameEditText.getText().toString())
                        && mSelectedProduct.getBrand().equals(mBrandEditText.getText().toString())
                        && mSelectedCategory.getName().equals(mCategoriesSpinner.getSelectedItem().toString())
                        && !TextUtils.isEmpty(mSelectedSubCategoryName)
                        && mSelectedSubCategoryName.equals(mSubCategoriesSpinner.getSelectedItem().toString()))
                {
                    return false;
                }
            }
        }
        catch (Exception ex)
        {
            return true;
        }

        return true;
    }

    private void addNewProduct()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);

        StoreProduct storeProduct = new StoreProduct();
        storeProduct.setName(mProductNameEditText.getText().toString());
        storeProduct.setBrand(mBrandEditText.getText().toString());
        storeProduct.setPrice(Double.parseDouble(mPriceEditText.getText().toString()));
        storeProduct.setNotes(mNotesEditText.getText().toString());

        // Set category code
        int selectedCategoryPosition = mCategoriesSpinner.getSelectedItemPosition() - 1;
        Category category = ContentManager.getInstance().getCategoryList().get(selectedCategoryPosition);
        storeProduct.setCategoryCode(category.getCode());

        // Set the subcategory code
        int selectedSubCategoryPosition = mSubCategoriesSpinner.getSelectedItemPosition() - 1;
        Category subCategory = category.getSubCategories().get(selectedSubCategoryPosition);
        storeProduct.setSubCategoryCode(subCategory.getCode());

        // Set image
        byte[] productImageViewByteArray = Utils.getByteArrayFromImageView(mProductImageView);
        String encodedImage = Base64.encodeToString(productImageViewByteArray, Base64.DEFAULT);
        storeProduct.setImageLink(encodedImage);

        RestClientManager.getInstance().addNewProductToStore(storeProduct, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                clearViewsAndValues();
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBar(mAddProductButton, getString(R.string.product_added), Snackbar.LENGTH_LONG);
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

    private void addExistingProduct()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        mStoreProductForUpload = new StoreProductForUpload();
        mStoreProductForUpload.setID(mSelectedProduct.getID());
        mStoreProductForUpload.setPrice(Double.parseDouble(mPriceEditText.getText().toString()));

        RestClientManager.getInstance().addExistingProductToStore(mStoreProductForUpload, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                clearViewsAndValues();
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBar(mAddProductButton, getString(R.string.product_added), Snackbar.LENGTH_LONG);
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

    private void clearViewsAndValues()
    {
        mProductNameEditText.setText("");
        mProductImageView.setImageResource(R.drawable.ic_place_holder_product);
        mCategoriesSpinner.setSelection(0);
        mSubCategoriesSpinner.setSelection(0);
        mBrandEditText.setText("");
        mPriceEditText.setText("");
        mNotesEditText.setText("");

        mSelectedProduct = null;
        mIsInAutoSelectionMode = true;
        mSelectedSubCategoryName = "";

        UIManager.getInstance().hideKeyboard(mProductNameEditText, getActivity());
    }

    @Subscribe
    public void refreshCroppedImage(ImageCroppedEvent imageCroppedEvent)
    {
        byte[] byteArray = ContentManager.getInstance().getNewProductCroppedImageByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mProductImageView.setImageBitmap(bitmap);
        mProductImageView.setTag(IMAGE_ADDED);
    }

}
