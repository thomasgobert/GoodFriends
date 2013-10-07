package gt.android.goodfriends;

import gt.android.bonsamis.R;
import gt.android.goodfriends.adapter.ParticipantAdapter;
import gt.android.goodfriends.bo.Depense;
import gt.android.goodfriends.bo.Participant;
import gt.android.goodfriends.bo.Sauvegarde;
import gt.android.goodfriends.sql.SauvegardeReaderContract.SauvegardeEntry;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SauvegardeActivity extends Activity {

	private final String KEY_HELP = "help_at_first_time_activity_sauvegarde";
	private ParticipantAdapter adapter;
	private ArrayAdapter<String> autoCompleteAdapter;
	private Sauvegarde sauvegarde;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sauvegarde);
		final ExpandableListView listview = (ExpandableListView) findViewById(R.id.lvParticipants);
		final EditText edDesc = (EditText) findViewById(R.id.edtDesc);
		final AutoCompleteTextView edParticipant = (AutoCompleteTextView) findViewById(R.id.edtParticipant);
		sauvegarde = new Sauvegarde(this.getIntent().getExtras()
				.get(SauvegardeEntry.KEY_SAUVEGARDE).toString());
		((TextView) findViewById(R.id.tvTitre))
				.setText(sauvegarde.getLibelle());
		adapter = new ParticipantAdapter(this, new ArrayList<Participant>());
		listview.setAdapter(adapter);
		registerForContextMenu(listview);
		autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, sauvegarde.getNoms());
		edParticipant.setAdapter(autoCompleteAdapter);
		edDesc.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				switch (actionId) {
				case EditorInfo.IME_ACTION_DONE:
					String nom = ((EditText) findViewById(R.id.edtParticipant))
							.getText().toString();
					final EditText edtSomme = (EditText) findViewById(R.id.edtSomme);
					float somme = 0;
					if (edtSomme.getText().length() > 0)
						somme = Float.parseFloat(edtSomme.getText().toString());
					final EditText edtDesc = (EditText) findViewById(R.id.edtDesc);
					final String desc = edtDesc.getText().toString();
					if (nom.length() == 0) {
						edParticipant.requestFocus();
						break;
					}
					sauvegarde.ajouterDepense(v.getContext(), nom, somme, desc);
					sauvegarde.repartirDettes();
					adapter.notifyDataSetChanged();
					autoCompleteAdapter.clear();
					autoCompleteAdapter.addAll(sauvegarde.getNoms());
					edtSomme.setText("");
					edtSomme.requestFocus();
					edtDesc.setText("");
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(edtDesc.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(edtSomme.getWindowToken(), 0);
					final float total = sauvegarde.getTotal();
					final int nb = sauvegarde.getParticipants().size();
					((TextView) findViewById(R.id.tvTotal)).setText(String
							.format("%.2f€ (%.2f€/per)", new Object[] { total,
									total / (nb == 0 ? 1f : (float) nb) }));
					return true;
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
				R.string.activity_sauvegarde_help_message));
		dg.setTitle(getResources().getString(
				R.string.activity_sauvegarde_help_title));
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
		sauvegarde.initialiseParticipants(this);
		sauvegarde.repartirDettes();
		adapter.setParticipants(this, sauvegarde.getParticipants());
		autoCompleteAdapter.clear();
		autoCompleteAdapter.addAll(sauvegarde.getNoms());
		final float total = sauvegarde.getTotal();
		final int nb = sauvegarde.getParticipants().size();
		((TextView) findViewById(R.id.tvTotal)).setText(String.format(
				"%.2f€ (%.2f€/per)", new Object[] { total,
						total / (nb == 0 ? 1f : (float) nb) }));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sauvegarde, menu);
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater menuInflater = getMenuInflater();
		
		ExpandableListContextMenuInfo infos = (ExpandableListContextMenuInfo) menuInfo;
		int groupPosition = ExpandableListView
				.getPackedPositionGroup(infos.packedPosition);
		int childPosition = ExpandableListView
				.getPackedPositionChild(infos.packedPosition);
		if (childPosition > -1) {
			final Depense d = (Depense) adapter.getChild(groupPosition,
					childPosition);
			if(d.getSomme() < 0)
				return;
		}
			
		menuInflater.inflate(R.menu.depense_item_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.btSupprimerDepense:
			// adapter.getch
			// Depense d = infos.
			ExpandableListContextMenuInfo infos = (ExpandableListContextMenuInfo) item
					.getMenuInfo();
			int groupPosition = ExpandableListView
					.getPackedPositionGroup(infos.packedPosition);
			int childPosition = ExpandableListView
					.getPackedPositionChild(infos.packedPosition);
			if (childPosition == -1) {
				final Participant p = (Participant) adapter
						.getGroup(groupPosition);
				sauvegarde.supprimerParticipant(this, p);
			} else {
				final Depense d = (Depense) adapter.getChild(groupPosition,
						childPosition);
				sauvegarde.supprimerDepense(this, d);
			}
			sauvegarde.repartirDettes();
			adapter.notifyDataSetChanged();
			autoCompleteAdapter.clear();
			autoCompleteAdapter.addAll(sauvegarde.getNoms());
		}
		float nouvelleSomme = 0;
		for (int i = 0; i < adapter.getParticipants().size(); i++)
			nouvelleSomme += adapter.getParticipants().get(i).getSomme();
		((TextView) findViewById(R.id.tvTotal)).setText(String.format("%1$s",
				nouvelleSomme));

		return true;
	}
}
