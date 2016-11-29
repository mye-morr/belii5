package com.better_computer.habitaid.form.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.core.ContactItem;

public class ContactListAdapter extends ArrayAdapter<ContactItem> {

    private Context context;
    private int resourceId;
    private List<ContactItem> contactItems;

    public ContactListAdapter(Context context, List<ContactItem> contactItems) {
        super(context, R.layout.list_item_contact, contactItems);
        this.context = context;
        this.resourceId = R.layout.list_item_contact;
        this.contactItems = contactItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView);
        final ContactItem contactItem = contactItems.get(position);
        ((TextView)convertView.findViewById(R.id.contact_name)).setText(contactItem.getName("unknown"));
        ((TextView)convertView.findViewById(R.id.contact_number)).setText(contactItem.getPhone());
        ((ImageView)convertView.findViewById(R.id.contact_icon)).setImageResource(R.drawable.contact);

        return convertView;
    }


    private View initView(View convertView){
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return vi.inflate(resourceId, null);
        }else{
            return convertView;
        }
    }
}