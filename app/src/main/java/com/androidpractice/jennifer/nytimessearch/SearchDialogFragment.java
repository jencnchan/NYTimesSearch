package com.androidpractice.jennifer.nytimessearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class SearchDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText beginDate;
    private Spinner sortOrder;
    private CheckBox arts;
    private CheckBox fashionAndStyle;
    private CheckBox sports;
    Bundle bundle;
    ArrayAdapter<CharSequence> spinnerAdapter;

    public SearchDialogFragment() {
    }

    public interface SearchDialogFragmentListener {
        void onFinishEditDialog(Bundle bundle);
    }

    public SearchDialogFragment newInstance() {
        SearchDialogFragment frag = new SearchDialogFragment();
        bundle = new Bundle();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        beginDate = (EditText) view.findViewById(R.id.beginDate_id);
        sortOrder = (Spinner) view.findViewById(R.id.sortOrder_spinner);

        spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.SortOrderItems, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOrder.setAdapter(spinnerAdapter);

        arts = (CheckBox) view.findViewById(R.id.arts_checkbox);
        fashionAndStyle = (CheckBox) view.findViewById(R.id.fashionAndStyle_checkbox);
        sports = (CheckBox) view.findViewById(R.id.sports_checkbox);

        // Fetch arguments from bundle and set title
        getDialog().setTitle("Select Filters");
        // Show soft keyboard automatically and request focus to field
        beginDate.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Setup a callback when the "Done" button is pressed on keyboard
        beginDate.setOnEditorActionListener(this);

    }


    // Fires whenever the textfield has an action performed
    // In this case, when the "Done" button is pressed
    // REQUIRES a 'soft keyboard' (virtual keyboard)
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            SearchDialogFragmentListener listener = (SearchDialogFragmentListener) getActivity();

            bundle.putString("beginDate_value", beginDate.getText().toString());
            bundle.putString("sortOrder_value", sortOrder.getSelectedItem().toString());

            StringBuilder sb = new StringBuilder();
            boolean artsIsChecked = arts.isChecked();
            if (artsIsChecked) {
                sb.append("Art").append(" ");
            }
            boolean fashionAndStyleIsChecked = fashionAndStyle.isChecked();
            if (fashionAndStyleIsChecked) {
                sb.append("Fashion & Style").append(" ");
            }
            boolean sportsIsChecked = sports.isChecked();
            if (artsIsChecked) {
                sb.append("Sports").append(" ");
            }

            bundle.putString("news_value", sb.toString());

            listener.onFinishEditDialog(bundle);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }

}
