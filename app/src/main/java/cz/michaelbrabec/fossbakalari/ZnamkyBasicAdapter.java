package cz.michaelbrabec.fossbakalari;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ZnamkyBasicAdapter extends RecyclerView.Adapter<ZnamkyBasicAdapter.MyViewHolder> {

    private List<ZnamkyItem> znamkyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView znamka, predmet, popis, datum, vaha;

        public MyViewHolder(View view) {
            super(view);
            znamka = view.findViewById(R.id.znamka);
            predmet = view.findViewById(R.id.predmet);
            popis = view.findViewById(R.id.popis);
            datum = view.findViewById(R.id.datum);
            vaha = view.findViewById(R.id.vaha);
        }
    }


    public ZnamkyBasicAdapter(List<ZnamkyItem> znamkyList) {
        this.znamkyList = znamkyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.znamky_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ZnamkyItem znamkyItem = znamkyList.get(position);
        holder.znamka.setText(znamkyItem.getZnamka());
        holder.vaha.setText("váha: " + znamkyItem.getVaha());
        holder.predmet.setText(znamkyItem.getPredmet());
        holder.popis.setText(znamkyItem.getPopis());
        String datum = znamkyItem.getDatum().substring(0, 12);
        holder.datum.setText(datum);

    }

    @Override
    public int getItemCount() {
        return znamkyList.size();
    }
}