package com.zhengxiaoyao0716.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;
import com.zhengxiaoyao0716.digimon2048.R;

public class EditInfoDialog {
    public static void editInfo(final Context context)
    {
        final EditText nameEditText = new EditText(context);
        SharedPreferences preferences
                = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        nameEditText.setPadding(0, 60, 0, 30);
        nameEditText.setGravity(Gravity.CENTER);
        String playerName = preferences.getString("playerName", "Unknown");
        nameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        nameEditText.setText(playerName);
        nameEditText.selectAll();
        final SharedPreferences.Editor editor = preferences.edit();
        new AlertDialog.Builder(context).setTitle(R.string.editDialogTitle)
                .setView(nameEditText)
                .setNegativeButton(R.string.cancelEdit, null)
                .setPositiveButton(R.string.saveEdit,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int i) {
                                String playerName = nameEditText.getText().toString();
                                if (playerName.length() > 2) {
                                    editor.putString("playerName", playerName);
                                    editor.commit();
                                } else Toast.makeText(context,
                                        R.string.editFailed, Toast.LENGTH_LONG).show();
                            }
                        }).show();
    }
}