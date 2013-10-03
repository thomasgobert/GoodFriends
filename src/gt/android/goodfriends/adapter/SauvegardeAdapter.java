package gt.android.goodfriends.adapter;

import gt.android.bonsamis.R;
import gt.android.goodfriends.bo.Sauvegarde;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SauvegardeAdapter extends BaseAdapter {

	private List<Sauvegarde> sauvegardes;

	public List<Sauvegarde> getSauvegardes() {
		return sauvegardes;
	}

	public void setSauvegardes(List<Sauvegarde> sauvegardes) {
		this.sauvegardes = sauvegardes;
	}

	private Context context;

	public SauvegardeAdapter(Context context, List<Sauvegarde> sauvergardes) {
		this.context = context;
		this.sauvegardes = sauvergardes;
		if (this.sauvegardes == null)
			this.sauvegardes = new ArrayList<Sauvegarde>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sauvegardes.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sauvegardes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private class ViewHolder {
		TextView tvLibelle;
		TextView tvNbParticipants;
		TextView tvDate;
		TextView tvSommeSauvegarde;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.sauvegarde_layout, parent,
					false);
			holder.tvLibelle = (TextView) convertView
					.findViewById(R.id.tvLibelle);
			holder.tvNbParticipants = (TextView) convertView
					.findViewById(R.id.tvNbParticipants);
			holder.tvSommeSauvegarde = (TextView) convertView
					.findViewById(R.id.tvSommeSauvegarde);
			holder.tvDate = (TextView) convertView
					.findViewById(R.id.tvDateCreation);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Sauvegarde s = (Sauvegarde) getItem(position);
		if (s != null) {
			holder.tvLibelle.setText(s.getLibelle());
			holder.tvNbParticipants.setText(String.format("%d", s
					.getParticipants().size()));
			holder.tvDate.setText(s.getDateCreation());
			holder.tvSommeSauvegarde.setText(String.format("%.2f€",
					s.getTotal()));
		}
		return convertView;
	}

}
