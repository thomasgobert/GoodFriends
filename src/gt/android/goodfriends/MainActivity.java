package gt.android.goodfriends;

import gt.android.bonsamis.R;
import gt.android.goodfriends.adapter.SauvegardeAdapter;
import gt.android.goodfriends.bo.Sauvegarde;
import gt.android.goodfriends.sql.SauvegardeReaderContract.SauvegardeEntry;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {

	private final String KEY_HELP = "help_at_first_time_activity_main";
	public final String KEY_EDT_LIBELLE = "libelle";
	private SauvegardeAdapter adapter;
	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EditText edtLibelle = ((EditText) findViewById(R.id.edtSauvegarde));
		if (savedInstanceState != null) {
			final String edtLibelleTemp = savedInstanceState
					.getString(KEY_EDT_LIBELLE);
			edtLibelle.setText(edtLibelleTemp);
		}
		listview = (ListView) findViewById(R.id.lvSauvegarde);
		adapter = new SauvegardeAdapter(this, new ArrayList<Sauvegarde>());
		listview.setAdapter(adapter);

		/*
		 * final AccountManager am = AccountManager.get(this); final Account[]
		 * accounts = am.getAccountsByType("com.google"); ((TextView)
		 * findViewById(R.id.tvWelcome)).setText(String.format(
		 * getResources().getString(R.string.activity_main_tvWelcome),
		 * accounts[0].name));
		 */
		registerForContextMenu(listview);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						SauvegardeActivity.class);
				i.putExtra(SauvegardeEntry.KEY_SAUVEGARDE, ((Sauvegarde) parent
						.getItemAtPosition(position)).getLibelle());
				startActivity(i);
			}
		});
		edtLibelle.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				switch (actionId) {
				case EditorInfo.IME_ACTION_DONE:
					EditText edtSauvegarde = (EditText) v
							.findViewById(R.id.edtSauvegarde);
					final String value = edtSauvegarde.getText().toString();
					Sauvegarde s = new Sauvegarde(value);
					s.ajouter(getApplicationContext());
					int i = 0;
					while (adapter.getSauvegardes() != null && adapter.getSauvegardes().indexOf(i) > -1 && adapter.getSauvegardes().get(i).getLibelle()
							.compareTo(value) < 0)
						i++;
					adapter.getSauvegardes().add(i, s);
					adapter.notifyDataSetChanged();
					((EditText) v.findViewById(R.id.edtSauvegarde)).setText("");
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(edtSauvegarde.getWindowToken(),
							0);
					break;
				}
				return false;
			}
		});
		SharedPreferences mPrefs = getSharedPreferences("userdetails",
				MODE_PRIVATE);
		final int premierBoot = mPrefs.getInt(KEY_HELP, 1);
		if (premierBoot == 1) {
			showHelpDialog();
		}

	}

	private void showHelpDialog() {
		Builder dg = new Builder(this);
		dg.setMessage(getResources().getString(
				R.string.activity_main_help_message));
		dg.setTitle(getResources().getString(R.string.activity_main_help_title));
		dg.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor ed = getSharedPreferences(
						"userdetails", MODE_PRIVATE).edit();
				ed.putInt(KEY_HELP, 0);
				ed.commit();
			}
		});
		dg.create().show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.setSauvegardes(Sauvegarde.getList(this));
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.muHelp:
			showHelpDialog();
			return true;
		}
		return false;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString(KEY_EDT_LIBELLE,
				((EditText) findViewById(R.id.edtSauvegarde)).getText()
						.toString());
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo infos = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.btSupprimerSauvegarde:
			// adapter.getch
			// Depense d = infos.
			Sauvegarde s = (Sauvegarde) adapter.getItem(infos.position);
			s.supprimer(getApplicationContext());
			adapter.getSauvegardes().remove(s);
			adapter.notifyDataSetChanged();
			return true;
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.sauvegarde_item_menu, menu);
	}

}
