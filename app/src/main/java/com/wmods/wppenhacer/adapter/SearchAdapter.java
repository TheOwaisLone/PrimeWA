package com.wmods.wppenhacer.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wmods.wppenhacer.R;
import com.wmods.wppenhacer.model.SearchableFeature;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter for displaying search results in a RecyclerView with section headers.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    
    private final List<Object> items; // Can be SearchableFeature or String (section header)
    private String searchQuery = "";
    private final OnFeatureClickListener listener;
    
    public interface OnFeatureClickListener {
        void onFeatureClick(SearchableFeature feature);
    }
    
    public SearchAdapter(OnFeatureClickListener listener) {
        this.items = new ArrayList<>();
        this.listener = listener;
    }
    
    public void setFeatures(List<SearchableFeature> newFeatures) {
        items.clear();
        
        // Group features by category
        Map<SearchableFeature.Category, List<SearchableFeature>> groupedFeatures = new LinkedHashMap<>();
        for (SearchableFeature feature : newFeatures) {
            groupedFeatures.computeIfAbsent(feature.getCategory(), k -> new ArrayList<>()).add(feature);
        }
        
        // Add items with section headers
        for (Map.Entry<SearchableFeature.Category, List<SearchableFeature>> entry : groupedFeatures.entrySet()) {
            items.add(entry.getKey().getDisplayName()); // Add section header
            items.addAll(entry.getValue()); // Add features in that section
        }
        
        notifyDataSetChanged();
    }
    
    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query : "";
    }
    
    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_section_header, parent, false);
            return new SectionHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result, parent, false);
            return new SearchResultViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SectionHeaderViewHolder) {
            ((SectionHeaderViewHolder) holder).bind((String) items.get(position));
        } else if (holder instanceof SearchResultViewHolder) {
            ((SearchResultViewHolder) holder).bind((SearchableFeature) items.get(position), searchQuery, listener);
        }
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }
    
    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView sectionTitle;
        private final View sectionIndicator;
        
        public SectionHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            sectionIndicator = itemView.findViewById(R.id.sectionIndicator);
        }
        
        public void bind(String title) {
            sectionTitle.setText(title);
        }
    }
    
    static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView summaryTextView;
        private final TextView categoryBadge;
        
        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.featureTitle);
            summaryTextView = itemView.findViewById(R.id.featureSummary);
            categoryBadge = itemView.findViewById(R.id.categoryBadge);
        }
        
        public void bind(SearchableFeature feature, String query, OnFeatureClickListener listener) {
            // Set title with highlighting
            titleTextView.setText(highlightText(feature.getTitle(), query));
            
            // Set summary with highlighting
            if (feature.getSummary() != null && !feature.getSummary().isEmpty()) {
                summaryTextView.setText(highlightText(feature.getSummary(), query));
                summaryTextView.setVisibility(View.VISIBLE);
            } else {
                summaryTextView.setVisibility(View.GONE);
            }
            
            // Set category badge with rounded pill shape
            categoryBadge.setText(feature.getCategory().getDisplayName().toUpperCase(Locale.ROOT));
            android.graphics.drawable.GradientDrawable badgeDrawable = new android.graphics.drawable.GradientDrawable();
            badgeDrawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            float radius = 12 * itemView.getResources().getDisplayMetrics().density;
            badgeDrawable.setCornerRadius(radius);
            badgeDrawable.setColor(getCategoryColor(feature.getCategory()));
            categoryBadge.setBackground(badgeDrawable);
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFeatureClick(feature);
                }
            });
        }
        
        private CharSequence highlightText(String text, String query) {
            if (text == null || query == null || query.trim().isEmpty()) {
                return text;
            }
            
            SpannableString spannable = new SpannableString(text);
            String lowerText = text.toLowerCase(Locale.ROOT);
            String lowerQuery = query.toLowerCase(Locale.ROOT).trim();
            
            int start = lowerText.indexOf(lowerQuery);
            if (start >= 0) {
                int end = start + lowerQuery.length();
                spannable.setSpan(
                        new BackgroundColorSpan(Color.parseColor("#3325D366")),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                spannable.setSpan(
                        new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            
            return spannable;
        }
        
        private int getCategoryColor(SearchableFeature.Category category) {
            switch (category) {
                case GENERAL:
                    return Color.parseColor("#2E7D32"); // Forest Green
                case CONVERSATION:
                    return Color.parseColor("#00897B"); // Teal
                case STATUS:
                    return Color.parseColor("#D81B60"); // Rose Pink
                case HOME:
                    return Color.parseColor("#3949AB"); // Indigo
                case PRIVACY:
                    return Color.parseColor("#1E88E5"); // Blue
                case CALLS:
                    return Color.parseColor("#00ACC1"); // Cyan
                case CUSTOMIZATION:
                    return Color.parseColor("#8E24AA"); // Purple
                case MEDIA:
                    return Color.parseColor("#FB8C00"); // Orange
                case RECORDINGS:
                    return Color.parseColor("#E53935"); // Red
                case MISC:
                    return Color.parseColor("#6D4C41"); // Brown
                case HOME_ACTIONS:
                    return Color.parseColor("#546E7A"); // Slate Blue
                default:
                    return Color.parseColor("#757575"); // Grey
            }
        }
    }
}
