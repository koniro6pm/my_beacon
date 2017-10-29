package com.example.emily.beaconside;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;

import java.util.List;

/**
 * Created by jennifer9759 on 2017/10/22.
 * 參考https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personEmail;
        ImageView personPhoto;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.members);
            personName = (TextView)itemView.findViewById(R.id.device_name);
            personEmail = (TextView)itemView.findViewById(R.id.person_email);
            personPhoto = (ImageView)itemView.findViewById(R.id.device_photo);
        }
    }

    List<Person> persons;
    RVAdapter(List<Person> persons){
        this.persons = persons;
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_setting, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.personName.setText(persons.get(i).name);
        personViewHolder.personEmail.setText(persons.get(i).email);
        personViewHolder.personPhoto.setImageResource(persons.get(i).photoId);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
