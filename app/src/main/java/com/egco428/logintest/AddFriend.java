package com.egco428.logintest;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Comment;

import java.util.List;

public class AddFriend extends AppCompatActivity {

    EditText name;
    EditText number;
    Button add;

    private ContDataSource dataSource;
    private ArrayAdapter<ContMessage> loginArrayAdapter;
    public static final String UserN = "username";
    public static final String Name = "name";
    public static final String Number = "number";
    String User;

    protected List<ContMessage> values;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);
        add = (Button) findViewById(R.id.addBtn);

        //Actionbar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f40d0d")));
        mActionBar.setTitle("Add Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button

        //Receive Username from SMSMapsActivity
        User = getIntent().getStringExtra(SMSMapsActivity.User);

        //SQLite Database
        dataSource = new ContDataSource(this);
        dataSource.open();
        values = dataSource.getAllComments(User);

        //Set Listview
        loginArrayAdapter = new loginArrayAdapter(this, 0, values);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(loginArrayAdapter); //push data in adapter into listview

        //Tab to delete from List and database
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int position, final long l) {

                if (loginArrayAdapter.getCount() > 0) {

                    final ContMessage login = loginArrayAdapter.getItem(position);
                    dataSource.deleteComment(login); // delete in database
                    view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            loginArrayAdapter.remove(login); // delete in listviewlist.remove(item);
                            view.setAlpha(1);
                        }
                    });
                }

            }
        });

        loginArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //put data to list
    class loginArrayAdapter extends ArrayAdapter<ContMessage> {
        Context context;
        List<ContMessage> objects;

        public loginArrayAdapter(Context context, int resource, List<ContMessage> objects) {
            super(context, resource, objects);
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ContMessage login = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_list, null);

            TextView txt = (TextView) view.findViewById(R.id.nameAdd);
            txt.setText(String.valueOf(login.getName()));

            TextView txt2 = (TextView) view.findViewById(R.id.numberAdd);
            txt2.setText(String.valueOf(login.getNumber()));

            return view;
        }

    }

    //add friend's name and phone number to SQlite Database
    public void add(View view) {

        ContMessage comment = null;

        String newName = name.getText().toString();
        String newNum = number.getText().toString();

        comment = dataSource.createCont(User, newNum, newName);
        dataSource.open();
        values = dataSource.getAllComments(User);
        loginArrayAdapter = new loginArrayAdapter(this, 0, values);
        listView.setAdapter(loginArrayAdapter);
    }

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}
