package com.hulzenga.ioi.android.app_003;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hulzenga.ioi.android.R;

/**
 * A small edit dialog from which the user can edit a selected monster name *
 */
public class MonsterEditDialog extends DialogFragment {

  private static final String TAG = "EDIT_DIALOG";

  // argument bundle keys used for passing information from the host to the
  // dialog and back again via the listener
  public static final String ARGUMENT_ID   = "id";
  public static final String ARGUMENT_NAME = "monsterName";

  // callback interface to communicate from this fragment to the host
  public interface EditDialogListener {
    public void onEditDialogPositiveClick(Bundle arguments);
  }

  // the host listener
  private EditDialogListener mListener = null;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    // try to cast the host to the appropriate listener class
    try {
      mListener = (EditDialogListener) activity;
    } catch (ClassCastException e) {
      Log.e(TAG, "ClassCastException: the host has not implemented the EditDialogListener interface");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    // dialog building necessities
    LayoutInflater inflator = getActivity().getLayoutInflater();
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    // extract data from the arguments bundle
    final Bundle arguments = getArguments();
    final String monsterName = arguments.getString(ARGUMENT_NAME);

    // define the custom view parts
    final View editDialog = inflator.inflate(R.layout.app_003_dialog_edit, null);
    final EditText editText = (EditText) editDialog.findViewById(R.id.app_003_editDialogText);

    editText.setText(monsterName);

    // setup random button
    final Button randomMonsterButton = (Button) editDialog.findViewById(R.id.app_003_editDialogRandomButton);
    randomMonsterButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        editText.setText(MonsterGenerator.randomMonster());
      }
    });

    // build the dialog
    builder.setView(editDialog).setMessage(getResources().getString(R.string.app_003_edit_dialog))
        .setPositiveButton(getResources().getString(android.R.string.ok), new OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            // if the editText has been changed from the original
            // monster name, call back to the listener
            if (!editText.getText().toString().equals(monsterName)) {
              arguments.putString(ARGUMENT_NAME, editText.getText().toString());
              mListener.onEditDialogPositiveClick(arguments);
            } else {
              // ignore
            }
          }
        }).setNegativeButton(getResources().getString(android.R.string.cancel), new OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        // do nothing
      }
    });
    return builder.create();
  }
}
