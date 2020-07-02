package com.azspc.vaultchar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.azspc.vaultchar.MainActivity.key_color;
import static com.azspc.vaultchar.MainActivity.key_icon;
import static com.azspc.vaultchar.MainActivity.sp;

class ProretyAdapter extends RecyclerView.Adapter<ProretyAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Property> properties;

    ProretyAdapter(Context context, List<Property> properties) {
        this.properties = properties;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProretyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.propertile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProretyAdapter.ViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.propertile.setText(property.getText());
        if (sp.getBoolean(key_color, true))
            holder.propertile.setTextColor(property.getColor());
        if (sp.getBoolean(key_icon, true))
            holder.propertile.setCompoundDrawablesWithIntrinsicBounds(property.getTypeIcon(), 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView propertile;

        ViewHolder(View view) {
            super(view);
            propertile = view.findViewById(R.id.prop_title);
        }
    }
}