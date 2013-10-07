package gt.android.goodfriends.adapter;

import gt.android.bonsamis.R;
import gt.android.goodfriends.bo.Depense;
import gt.android.goodfriends.bo.Participant;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ParticipantAdapter extends BaseExpandableListAdapter {

	Context context;
	List<Participant> participants;
	OnClickListener listener;

	public OnClickListener getOnClickListener() {
		return listener;
	}

	public void setOnClickListener(OnClickListener value) {
		listener = value;
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(Context context, List<Participant> value) {
		this.context = context;
		participants = value;
		notifyDataSetChanged();
	}

	public ParticipantAdapter(Context context, List<Participant> participants) {
		this.context = context;
		this.participants = participants;
	}

	private class ViewHolder {
		TextView tvNom;
		TextView tvSomme;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		Participant p = (Participant) getGroup(arg0);
		if (p == null)
			return null;
		return p.getDepenses().get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		Participant p = (Participant) getGroup(arg0);
		if (p != null)
			return p.getDepenses().get(arg1).getId();
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.depense_layout, parent, false);
		Depense d = (Depense) getChild(groupPosition, childPosition);
		if (d != null) {
			((TextView) convertView.findViewById(R.id.tvSomme)).setText(String
					.format("%.2f€", d.getSomme()));
			((TextView) convertView.findViewById(R.id.tvDesc)).setText(String
					.format("%1$s", d.getDesc()));
			((TextView) convertView.findViewById(R.id.tvFlag)).setText(String
					.format("%1$s", d.getFlag()));
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		Participant p = (Participant) getGroup(arg0);
		if (p == null)
			return -1;
		return p.getDepenses().size();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return participants.get(arg0);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return participants.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.participant_layout, parent,
					false);
			holder.tvNom = (TextView) convertView.findViewById(R.id.tvNom);
			holder.tvSomme = (TextView) convertView
					.findViewById(R.id.tvSommeTotal);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Participant p = (Participant) getGroup(groupPosition);
		if (p != null) {
			holder.tvNom.setText(p.getNom());
			holder.tvSomme.setText(String.format("%.2f€", p.getSomme()));
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
