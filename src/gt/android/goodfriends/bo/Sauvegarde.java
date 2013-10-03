package gt.android.goodfriends.bo;

import gt.android.goodfriends.sql.BonsamisDbHelper;
import gt.android.goodfriends.sql.DepenseReaderContract.DepenseEntry;
import gt.android.goodfriends.sql.SauvegardeReaderContract.SauvegardeEntry;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.text.format.Time;

public class Sauvegarde {

	private int id;
	private String libelle;
	private String dateCreation;

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public int getId() {
		return id;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	private List<Participant> participants;

	public List<Participant> getParticipants() {
		return participants;
	}

	public void ajouterParticipant(Participant p) {
		boolean inserted = false;
		for (int i = 0; i < participants.size(); i++) {
			if (p.getNom().compareTo(participants.get(i).getNom()) < 0) {
				participants.add(i, p);
				inserted = true;
				break;
			}
		}
		if (!inserted)
			participants.add(p);
	}

	public void setParticipants(List<Participant> value) {
		this.participants = value;
	}

	public Sauvegarde(String libelle) {
		this(-1, libelle, Sauvegarde.getTime());
	}

	public Sauvegarde(int id, String libelle, String dateCreation) {
		this.id = id;
		this.libelle = libelle;
		this.dateCreation = dateCreation;
		this.participants = new ArrayList<Participant>();
	}

	public void repartirDettes() {
		float moyenne = getTotal() / participants.size();
		for (int i = 0; i < participants.size(); i++) {
			participants.get(i).supprimerDettes();
		}
		for (int i = 0; i < participants.size(); i++) {
			Participant p = participants.get(i);
			if (p.getSommeAvecDette() > moyenne) {
				float difference = Float.parseFloat(String.format("%.2f",
						p.getSommeAvecDette() - moyenne).replace(",", "."));
				while (difference > 0.01) {
					for (int j = 0; j < participants.size(); j++) {
						if (j == i)
							continue;
						Participant p2 = participants.get(j);
						if (p2.getSommeAvecDette() < moyenne) {
							float diff2 = moyenne - p2.getSommeAvecDette();
							float res;
							if (diff2 >= difference) {
								res = difference;
								difference = 0;
							} else {
								res = diff2;
								difference = difference - diff2;
							}
							if (res > 0)
								p2.rembourser(p, res);
							break;
						}
					}
				}
			}
		}
	}

	public float getTotal() {
		float somme = 0;
		for (int i = 0; i < participants.size(); i++) {
			somme += participants.get(i).getSomme();
		}
		return somme;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o.getClass() != Sauvegarde.class)
			return false;
		return this.hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return libelle.hashCode();
	}

	public static List<Sauvegarde> getList(Context ctx) {
		List<Sauvegarde> sauvegardes = new ArrayList<Sauvegarde>();
		final BonsamisDbHelper helper = new BonsamisDbHelper(ctx);
		final SQLiteDatabase dbR = helper.getReadableDatabase();
		final String[] projection = { SauvegardeEntry._ID,
				SauvegardeEntry.KEY_SAUVEGARDE,
				SauvegardeEntry.KEY_DATE_CREATION };
		final Cursor cSauvergarde = dbR.query(SauvegardeEntry.TABLE_NAME,
				projection, "", null, null, null,
				SauvegardeEntry.KEY_SAUVEGARDE + " ASC");
		while (cSauvergarde.moveToNext()) {
			Sauvegarde s = new Sauvegarde(
					cSauvergarde.getInt(cSauvergarde
							.getColumnIndexOrThrow(SauvegardeEntry._ID)),
					cSauvergarde.getString(cSauvergarde
							.getColumnIndexOrThrow(SauvegardeEntry.KEY_SAUVEGARDE)),
					cSauvergarde.getString(cSauvergarde
							.getColumnIndexOrThrow(SauvegardeEntry.KEY_DATE_CREATION)));
			s.initialiseParticipants(ctx);
			sauvegardes.add(s);
		}
		return sauvegardes;
	}

	public void initialiseParticipants(Context ctx) {
		participants = new ArrayList<Participant>();
		final BonsamisDbHelper helper = new BonsamisDbHelper(ctx);
		final SQLiteDatabase dbR = helper.getReadableDatabase();
		final String[] projection = { DepenseEntry._ID,
				DepenseEntry.COLUMN_SAUVEGARDE,
				DepenseEntry.COLUMN_PARTICIPANT, DepenseEntry.COLUMN_SOMME,
				DepenseEntry.COLUMN_DESC, DepenseEntry.COLUMN_FLAG };
		final String selection = DepenseEntry.COLUMN_SAUVEGARDE + " = ?";
		final String[] args = { getLibelle() };
		final Cursor cDepense = dbR.query(DepenseEntry.TABLE_NAME, projection,
				selection, args, null, null, DepenseEntry.COLUMN_PARTICIPANT
						+ " ASC");

		while (cDepense.moveToNext()) {
			final int id = cDepense.getInt(cDepense
					.getColumnIndex(DepenseEntry._ID));
			final float somme = cDepense.getFloat(cDepense
					.getColumnIndex(DepenseEntry.COLUMN_SOMME));
			Participant newP = new Participant(cDepense.getString(cDepense
					.getColumnIndex(DepenseEntry.COLUMN_PARTICIPANT)));
			final String sauvegarde = cDepense.getString(cDepense
					.getColumnIndex(DepenseEntry.COLUMN_SAUVEGARDE));
			final String desc = cDepense.getString(cDepense
					.getColumnIndex(DepenseEntry.COLUMN_DESC));
			final String flag = cDepense.getString(cDepense
					.getColumnIndex(DepenseEntry.COLUMN_FLAG));
			if (participants.contains(newP)) {
				newP = participants.get(participants.indexOf(newP));
			} else {
				newP.setSauvegarde(this);
				ajouterParticipant(newP);
			}
			newP.getDepenses().add(
					new Depense(id, sauvegarde, newP, somme, desc, flag));
		}
	}

	public void ajouter(Context ctx) {
		final BonsamisDbHelper helper = new BonsamisDbHelper(ctx);
		final SQLiteDatabase dbW = helper.getWritableDatabase();
		final ContentValues values = new ContentValues();
		values.put(SauvegardeEntry.KEY_SAUVEGARDE, getLibelle());
		values.put(SauvegardeEntry.KEY_DATE_CREATION, Sauvegarde.getTime());
		this.id = (int) dbW.insert(SauvegardeEntry.TABLE_NAME, null, values);
	}

	private static String getTime() {
		Time t = new Time();
		t.setToNow();
		return DateFormat.format("dd-MM-yy kk:mm", new java.util.Date())
				.toString();
	}

	public void ajouterDepense(Context ctx, String nom, float somme, String desc) {
		final BonsamisDbHelper helper = new BonsamisDbHelper(ctx);
		final SQLiteDatabase dbW = helper.getWritableDatabase();
		final ContentValues values = new ContentValues();
		final String flag = Sauvegarde.getTime();
		values.put(DepenseEntry.COLUMN_SAUVEGARDE, getLibelle());
		values.put(DepenseEntry.COLUMN_PARTICIPANT, nom);
		values.put(DepenseEntry.COLUMN_SOMME, somme);
		values.put(DepenseEntry.COLUMN_DESC, desc);
		values.put(DepenseEntry.COLUMN_FLAG, flag);
		int id = (int) dbW.insert(DepenseEntry.TABLE_NAME, null, values);
		Participant p = new Participant(nom);
		if (participants.indexOf(p) == -1) {
			p.setSauvegarde(this);
			ajouterParticipant(p);
		} else
			p = participants.get(participants.indexOf(p));
		p.getDepenses().add(
				new Depense((int) id, getLibelle(), p, somme, desc, flag));
	}

	public void supprimerParticipant(Context ctx, Participant participant) {
		final BonsamisDbHelper helper = new BonsamisDbHelper(ctx);
		final SQLiteDatabase dbW = helper.getWritableDatabase();
		String selection = DepenseEntry.COLUMN_SAUVEGARDE + " = ? AND "
				+ DepenseEntry.COLUMN_PARTICIPANT + " = ?";
		String[] args = { getLibelle(), participant.getNom() };
		dbW.delete(DepenseEntry.TABLE_NAME, selection, args);
		getParticipants().remove(participant);
	}

	public void supprimerDepense(Context ctx, Depense depense) {
		final BonsamisDbHelper helper = new BonsamisDbHelper(ctx);
		final SQLiteDatabase dbW = helper.getWritableDatabase();
		final Participant p = depense.getParticipant();
		final String selection = DepenseEntry._ID + " = ?";
		final String[] args = { String.format("%1$s", depense.getId()) };
		dbW.delete(DepenseEntry.TABLE_NAME, selection, args);
		p.retirerDepense(depense);
		if (p.getDepenses().size() == 0)
			getParticipants().remove(p);
	}

	public void supprimer(Context ctx) {
		final BonsamisDbHelper helper = new BonsamisDbHelper(ctx);
		final SQLiteDatabase dbW = helper.getWritableDatabase();
		String selection = SauvegardeEntry._ID + " = ?";
		String[] args = { String.format("%s", getId()) };
		dbW.delete(SauvegardeEntry.TABLE_NAME, selection, args);
		dbW.delete(DepenseEntry.TABLE_NAME, DepenseEntry.COLUMN_SAUVEGARDE
				+ " = ?", new String[] { getLibelle() });
	}
}
