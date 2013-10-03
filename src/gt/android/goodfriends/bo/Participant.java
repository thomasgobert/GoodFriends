package gt.android.goodfriends.bo;

import java.util.ArrayList;
import java.util.List;

public class Participant {

	private Sauvegarde sauvegarde;
	private List<Depense> depenses;
	private String nom;

	public Sauvegarde getSauvegarde() {
		return sauvegarde;
	}

	public void setSauvegarde(Sauvegarde value) {
		this.sauvegarde = value;
	}

	public List<Depense> getDepenses() {
		return depenses;
	}

	public void setDepenses(List<Depense> value) {
		this.depenses = value;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String value) {
		this.nom = value;
	}

	public float getSomme() {
		float somme = 0;
		for (int i = 0; i < depenses.size(); i++) {
			float val = depenses.get(i).getSomme();
			if (val < 0)
				continue;
			somme += val;
		}
		return somme;
	}

	public Participant(String nom) {
		this.nom = nom;
		this.depenses = new ArrayList<Depense>();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o.getClass() != Participant.class)
			return false;
		return this.hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return nom.hashCode();
	}

	public void retirerDepense(Depense d) {
		depenses.remove(d);
	}

	public void rembourser(Participant p, float somme) {
		depenses.add(new Depense(-1, sauvegarde.getLibelle(), this, somme * -1,
				"-->", p.getNom()));
	}

	public float getSommeAvecDette() {
		float somme = 0;
		for (int i = 0; i < depenses.size(); i++) {
			float val = depenses.get(i).getSomme();
			if (val < 0)
				val *= -1;
			somme += val;
		}
		return somme;
	}
	
	public void supprimerDettes() {
		List<Depense> deps = new ArrayList<Depense>();
		for (int i = 0; i < depenses.size(); i++) {
			float val = depenses.get(i).getSomme();
			if (val > 0)
				deps.add(depenses.get(i));
		}
		this.depenses = deps;
	}
}
