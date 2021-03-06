package com.hackforchange.views.projects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.models.projects.Project;
import com.hackforchange.views.dialogs.PickDateDialog;
import com.hackforchange.views.dialogs.PickDateDialogListener;

/*
 * Presents an activity that lets you add a new project
 * Pressing the back key will exit the activity without adding a project
 */
// TODO: Make sure required text fields are not empty
// TODO: make sure activity dates don't go out of project dates
// TODO: make sure repeating alarms stop when the activity ends
public class AddProjectActivity extends SherlockFragmentActivity implements PickDateDialogListener {
  protected int mYear, mMonth, mDay;
  protected EditText title;
  protected EditText startDate;
  protected EditText endDate;
  protected EditText notes;
  protected Button submitButton;
  protected boolean startOrEnd; // used to distinguish between start date and end date field
  protected Project p;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_addproject);
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // entering the start date
    startDate = (EditText) findViewById(R.id.startDate);
    startDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
    // not doing this means the first click simply focuses the text field
    startDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startOrEnd = true;
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Bundle bundle = new Bundle();
        try {
          Date date = parser.parse(startDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in EditProjectActivity (which is a subclass of this one) for editing a project
        } catch (ParseException e) {
        }
        try {
          Date date = parser.parse(endDate.getText().toString());
          bundle.putLong("maxdate", date.getTime());
        } catch (ParseException e) {
        }
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getSupportFragmentManager(), "datepicker");
      }
    });

    // entering the end date
    endDate = (EditText) findViewById(R.id.endDate);
    endDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
    // not doing this means the first click simply focuses the text field
    endDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startOrEnd = false;
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Bundle bundle = new Bundle();
        try {
          Date date = parser.parse(endDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in EditProjectActivity (which is a subclass of this one) for editing a project
        } catch (ParseException e) {
        }
        try {
          Date date = parser.parse(startDate.getText().toString());
          bundle.putLong("mindate", date.getTime());
        } catch (ParseException e) {
        }
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getSupportFragmentManager(), "datepicker");
      }
    });

    title = (EditText) findViewById(R.id.title);
    notes = (EditText) findViewById(R.id.notes);

    submitButton = (Button) findViewById(R.id.submitbutton);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        p = new Project();
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        try {
          Date date = parser.parse(startDate.getText().toString());
          p.setStartDate(date.getTime());
          date = parser.parse(endDate.getText().toString());
          date.setHours(23);
          date.setMinutes(59);
          p.setEndDate(date.getTime());
        } catch (ParseException e) {
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage, Toast.LENGTH_SHORT).show();
          return;
        }
        p.setTitle(title.getText().toString());
        if (p.getTitle().equals("")) {
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage, Toast.LENGTH_SHORT).show();
          return;
        }
        p.setNotes(notes.getText().toString());

        ProjectDAO pDao = new ProjectDAO(getApplicationContext());
        pDao.addProject(p);
        finish();
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  @Override
  public void setDate(String date) {
    if (startOrEnd)
      startDate.setText(date); //sets the chosen date in the text view
    else
      endDate.setText(date); //sets the chosen date in the text view
  }

}