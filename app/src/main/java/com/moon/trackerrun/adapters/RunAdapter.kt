package com.moon.trackerrun.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.moon.trackerrun.R
import com.moon.trackerrun.db.Run
import com.bumptech.glide.Glide
import com.moon.trackerrun.databinding.ItemRunBinding
import com.moon.trackerrun.utils.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    // ListDiffer to efficiently deal with changes in the RecyclerView
    val differ = AsyncListDiffer(this, diffCallback)

    inner class RunViewHolder(val binding: ItemRunBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            ItemRunBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        // set item data
        holder.binding.apply {
            Glide.with(root).load(run.img).into(ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            "${run.avgSpeedInKMH}km/h".also {
                tvAvgSpeed.text = it
            }
            "${run.distanceInMeters / 1000f}km".also {
                tvDistance.text = it
            }
            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
            "${run.caloriesBurned}kcal".also {
                tvCalories.text = it
            }
        }
    }
}