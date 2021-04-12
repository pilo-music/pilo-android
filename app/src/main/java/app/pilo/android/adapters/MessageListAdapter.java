package app.pilo.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.models.Message;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.LikeListAdapterViewHolder> {
    private Context context;
    private List<Message> messages;

    public MessageListAdapter(WeakReference<Context> context, List<Message> messages) {
        this.context = context.get();
        this.messages = messages;
    }

    @NonNull
    @Override
    public LikeListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, viewGroup, false);
        return new LikeListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeListAdapterViewHolder holder, final int position) {
        final Message message = messages.get(position);
        if (message.getSender() == 1) {
            holder.tv_message_sender.setText(context.getString(R.string.admin));
            holder.tv_message_sender.setBackground(context.getDrawable(R.drawable.gray_round_box_background));
            holder.tv_message_sender.setTextColor(Color.WHITE);
        } else {
            holder.tv_message_sender.setText(context.getString(R.string.you));
            holder.tv_message_sender.setBackground(context.getDrawable(R.drawable.primary_light_round_box_background));
        }

        holder.tv_message_subject.setText(message.getSubject());
        holder.tv_message_text.setText(message.getText());
        holder.tv_message_created_at.setText(message.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class LikeListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_message_subject)
        TextView tv_message_subject;
        @BindView(R.id.tv_message_text)
        TextView tv_message_text;
        @BindView(R.id.tv_message_created_at)
        TextView tv_message_created_at;
        @BindView(R.id.tv_message_sender)
        TextView tv_message_sender;

        LikeListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}