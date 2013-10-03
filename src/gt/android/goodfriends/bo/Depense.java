package gt.android.goodfriends.bo;

public class Depense {

	public Depense(int id,String sauvegarde,Participant participant,float somme,String desc,String flag){
		this.id = id;
		this.sauvegarde = sauvegarde;
		this.participant = participant;
		this.somme = somme;
		this.desc = desc;
		this.flag = flag;
	}
	
	private int id;
	public int getId(){
		return id;
	}
	public void setId(int value){
		this.id = value;
	}
	
	private String sauvegarde;
	public String getSauvegarde(){
		return sauvegarde;
	}
	public void setSauvegarde(String value){
		this.sauvegarde = value;
	}
	
	private Participant participant;
	public Participant getParticipant(){
		return participant;
	}
	public void setparticipant(Participant value){
		this.participant = value;
	}
	
	private float somme;
	public float getSomme(){
		return somme;
	}
	public void setSomme(float value){
		this.somme = value;
	}
	
	private String desc;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	private String flag;
	public String getFlag(){
		return flag;
	}
	
	public void setFlag(String flag){
		this.flag = flag;
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o.getClass() != Depense.class)
			return false;
		return this.hashCode() == o.hashCode();
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return getId();
	}
	
	
}
