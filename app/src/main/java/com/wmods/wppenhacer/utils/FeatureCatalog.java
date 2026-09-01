package com.wmods.wppenhacer.utils;

import android.content.Context;

import com.wmods.wppenhacer.R;
import com.wmods.wppenhacer.model.SearchableFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central catalog of all searchable features in the WaEnhancer app.
 * This class builds and maintains a complete index of all features from
 * preference XMLs.
 */
public class FeatureCatalog {

    private static List<SearchableFeature> features;

    /**
     * Initialize and return the complete feature catalog.
     */
    public static List<SearchableFeature> getAllFeatures(Context context) {
        if (features == null) {
            features = buildFeatureCatalog(context);
        }
        return features;
    }

    /**
     * Search features by query string.
     * Returns all features that match the search query.
     */
    public static List<SearchableFeature> search(Context context, String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return getAllFeatures(context).stream()
                .filter(feature -> feature.matches(query))
                .collect(Collectors.toList());
    }

    /**
     * Build the complete feature catalog from all preference XMLs.
     */
    private static List<SearchableFeature> buildFeatureCatalog(Context context) {
        List<SearchableFeature> catalog = new ArrayList<>();

        addGeneralFeatures(context, catalog);
        addConversationFeatures(context, catalog);
        addStatusFeatures(context, catalog);
        addHomeFeatures(context, catalog);
        addPrivacyFeatures(context, catalog);
        addCallsFeatures(context, catalog);
        addCustomizationFeatures(context, catalog);
        addMediaFeatures(context, catalog);
        addMiscFeatures(context, catalog);
        addHomeActions(context, catalog);

        return catalog;
    }

    private static void addGeneralFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("update_check",
                context.getString(R.string.update_check),
                context.getString(R.string.update_check_sum),
                SearchableFeature.Category.GENERAL,
                SearchableFeature.FragmentType.GENERAL,
                null,
                Arrays.asList("update", "check", "automatic")));

        catalog.add(new SearchableFeature("bypass_version_check",
                context.getString(R.string.disable_version_check),
                context.getString(R.string.disable_version_check_sum),
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("version", "check", "bypass", "expiration")));

        catalog.add(new SearchableFeature("hide_launcher_icon",
                context.getString(R.string.hide_launcher_icon),
                context.getString(R.string.hide_launcher_icon_sum),
                SearchableFeature.Category.GENERAL,
                SearchableFeature.FragmentType.GENERAL,
                null,
                Arrays.asList("hide", "launcher", "icon", "drawer", "app")));

        catalog.add(new SearchableFeature("ampm",
                context.getString(R.string.ampm),
                null,
                SearchableFeature.Category.GENERAL,
                SearchableFeature.FragmentType.GENERAL,
                null,
                Arrays.asList("time", "12", "hour", "format")));

        catalog.add(new SearchableFeature("segundos",
                context.getString(R.string.segundosnahora),
                context.getString(R.string.segundosnahora_sum),
                SearchableFeature.Category.GENERAL,
                SearchableFeature.FragmentType.GENERAL,
                null,
                Arrays.asList("seconds", "timestamp", "time")));

        catalog.add(new SearchableFeature("text_in_hour",
                context.getString(R.string.textonahora),
                context.getString(R.string.textonahora_sum),
                SearchableFeature.Category.GENERAL,
                SearchableFeature.FragmentType.GENERAL,
                null,
                Arrays.asList("text", "timestamp", "custom")));

        catalog.add(new SearchableFeature("enablelogs",
                context.getString(R.string.verbose_logs),
                null,
                SearchableFeature.Category.GENERAL,
                SearchableFeature.FragmentType.GENERAL,
                null,
                Arrays.asList("logs", "debug", "verbose")));
    }

    private static void addHomeFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("pure_messaging_mode",
                context.getString(R.string.pure_messaging_mode),
                context.getString(R.string.pure_messaging_mode_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("pure", "messaging", "distraction", "hide", "tabs")));

        catalog.add(new SearchableFeature("hidetabs",
                context.getString(R.string.hide_tabs_on_home),
                context.getString(R.string.hide_tabs_on_home_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("hide", "tabs", "home", "groups", "communities", "calls")));

        catalog.add(new SearchableFeature("channels",
                context.getString(R.string.disable_channels),
                context.getString(R.string.disable_channels_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("channels", "disable", "hide")));

        catalog.add(new SearchableFeature("removechannel_rec",
                context.getString(R.string.remove_channel_recomendations),
                context.getString(R.string.remove_channel_recomendations_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("channel", "recommendations", "remove")));

        catalog.add(new SearchableFeature("separategroups",
                context.getString(R.string.separate_groups),
                context.getString(R.string.separate_groups_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("separate", "groups", "filter")));

        catalog.add(new SearchableFeature("filtergroups",
                context.getString(R.string.new_ui_group_filter),
                context.getString(R.string.new_ui_group_filter_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("filter", "groups", "ui")));

        catalog.add(new SearchableFeature("chatfilter",
                context.getString(R.string.novofiltro),
                context.getString(R.string.novofiltro_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("search", "filter", "icon", "bar")));

        catalog.add(new SearchableFeature("configui_mode",
                context.getString(R.string.configui),
                context.getString(R.string.configui_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("ui", "style", "ios", "bubble", "home")));

        catalog.add(new SearchableFeature("menuwicon",
                context.getString(R.string.menuwicon),
                context.getString(R.string.menuwicon_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("menu", "icons")));

        catalog.add(new SearchableFeature("floating_bottom_bar",
                context.getString(R.string.floating_bottom_bar),
                context.getString(R.string.floating_bottom_bar_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("floating", "bottom", "bar", "navigation")));

        catalog.add(new SearchableFeature("floating_bottom_bar_radius",
                context.getString(R.string.floating_bottom_bar_radius),
                null,
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("floating", "bottom", "bar", "radius")));

        catalog.add(new SearchableFeature("buttonaction",
                context.getString(R.string.show_menu_buttons_as_icons),
                context.getString(R.string.show_menu_buttons_as_icons_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("menu", "icons", "buttons")));

        catalog.add(new SearchableFeature("shownamehome",
                context.getString(R.string.showname),
                context.getString(R.string.showname_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("name", "profile", "title")));

        catalog.add(new SearchableFeature("showbiohome",
                context.getString(R.string.showbio),
                context.getString(R.string.showbio_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("bio", "status", "toolbar")));

        catalog.add(new SearchableFeature("show_dndmode",
                context.getString(R.string.show_dnd_button),
                context.getString(R.string.show_dnd_button_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("dnd", "do not disturb", "button")));

        catalog.add(new SearchableFeature("show_freezeLastSeen",
                context.getString(R.string.show_freezeLastSeen_button),
                context.getString(R.string.show_freezeLastSeen_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("freeze", "last seen", "button", "toolbar")));

        catalog.add(new SearchableFeature("newchat",
                context.getString(R.string.enable_new_chat_button),
                context.getString(R.string.enable_new_chat_button_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("new", "chat", "button")));

        catalog.add(new SearchableFeature("restartbutton",
                context.getString(R.string.enable_restart_button),
                context.getString(R.string.enable_restart_button_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("restart", "reboot", "button")));

        catalog.add(new SearchableFeature("open_wae",
                context.getString(R.string.enable_wa_enhancer_button),
                context.getString(R.string.enable_wa_enhancer_button_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("wa enhancer", "open", "button")));

        catalog.add(new SearchableFeature("filterseen",
                context.getString(R.string.enable_filter_chats),
                context.getString(R.string.enable_filter_chats_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("filter", "chats", "unseen")));

        catalog.add(new SearchableFeature("metaai",
                context.getString(R.string.disable_metaai),
                context.getString(R.string.disable_metaai_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("meta", "ai", "disable")));

        catalog.add(new SearchableFeature("disable_profile_status",
                context.getString(R.string.disable_status_in_the_profile_photo),
                context.getString(R.string.disable_status_in_the_profile_photo_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("status", "profile", "photo", "circle")));

        catalog.add(new SearchableFeature("pinnedlimit",
                context.getString(R.string.disable_pinned_limit),
                context.getString(R.string.disable_pinned_limit_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("pinned", "limit", "chats", "unlimited")));

        catalog.add(new SearchableFeature("animation_list",
                context.getString(R.string.list_animations_home_screen),
                context.getString(R.string.list_animations_home_screen_sum),
                SearchableFeature.Category.HOME,
                SearchableFeature.FragmentType.HOME,
                null,
                Arrays.asList("animation", "list", "home")));
    }

    private static void addConversationFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("admin_grp",
                context.getString(R.string.show_admin_group_icon),
                context.getString(R.string.show_admin_group_icon_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("admin", "group", "icon")));

        catalog.add(new SearchableFeature("floatingmenu",
                context.getString(R.string.new_context_menu_ui),
                context.getString(R.string.new_context_menu_ui_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("floating", "menu", "context", "ios")));

        catalog.add(new SearchableFeature("antieditmessages",
                context.getString(R.string.show_edited_message_history),
                context.getString(R.string.show_edited_message_history_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("edited", "history", "message")));

        catalog.add(new SearchableFeature("copy_selection_message",
                context.getString(R.string.copy_selection_message_title),
                context.getString(R.string.copy_selection_message_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("copy", "selection", "partial", "text")));

        catalog.add(new SearchableFeature("stamp_copied_message",
                context.getString(R.string.stamp_copied_messages),
                context.getString(R.string.stamp_copied_messages_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("copy", "stamp", "copied", "messages")));

        catalog.add(new SearchableFeature("hidetag",
                context.getString(R.string.hidetag),
                context.getString(R.string.hidetag_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("forwarded", "tag", "hide")));

        catalog.add(new SearchableFeature("removeforwardlimit",
                context.getString(R.string.removeforwardlimit),
                context.getString(R.string.removeforwardlimit_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("forward", "limit", "remove", "share")));

        catalog.add(new SearchableFeature("broadcast_tag",
                context.getString(R.string.show_chat_broadcast_icon),
                context.getString(R.string.show_chat_broadcast_icon_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("broadcast", "icon", "tag")));

        catalog.add(new SearchableFeature("removeseemore",
                context.getString(R.string.remove_see_more_button),
                context.getString(R.string.remove_see_more_button_),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("see more", "button", "remove")));

        catalog.add(new SearchableFeature("disable_swipe_up_in_group",
                context.getString(R.string.disable_swipe_up_in_group),
                context.getString(R.string.disable_swipe_up_in_group_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("swipe", "up", "group")));

        catalog.add(new SearchableFeature("doubletap2like",
                context.getString(R.string.double_click_to_react),
                context.getString(R.string.double_click_to_like_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("double", "tap", "click", "react", "like")));

        catalog.add(new SearchableFeature("doubletap2like_emoji",
                context.getString(R.string.custom_reaction),
                context.getString(R.string.custom_reaction_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("reaction", "emoji", "custom")));

        catalog.add(new SearchableFeature("google_translate",
                context.getString(R.string.google_translate),
                context.getString(R.string.google_translate_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("translate", "google", "language")));

        catalog.add(new SearchableFeature("jump_first_message",
                context.getString(R.string.jump_first_message),
                context.getString(R.string.jump_first_message_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("jump", "first", "message", "top")));

        catalog.add(new SearchableFeature("about_contact_picker",
                context.getString(R.string.show_contact_bio),
                context.getString(R.string.show_contact_bio_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("contact", "bio", "about")));

        catalog.add(new SearchableFeature("force_disable_emojis",
                context.getString(R.string.force_disable_emojis),
                context.getString(R.string.force_disable_emojis_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("disable", "emojis", "system")));

        catalog.add(new SearchableFeature("disable_defemojis",
                context.getString(R.string.disable_default_emojis),
                context.getString(R.string.disable_default_emojis_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("emoji", "default", "disable")));

        catalog.add(new SearchableFeature("animation_emojis",
                context.getString(R.string.animation_emojis),
                context.getString(R.string.animation_emojis_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("animation", "emojis", "large")));

        catalog.add(new SearchableFeature("disable_voice_note",
                context.getString(R.string.disable_voice_note),
                context.getString(R.string.disable_voice_note_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("voice", "note", "disable")));

        catalog.add(new SearchableFeature("disable_sensor_proximity",
                context.getString(R.string.disable_proximity_sensor),
                context.getString(R.string.disable_proximity_sensor_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("proximity", "sensor", "screen", "ear")));

        catalog.add(new SearchableFeature("proximity_audios",
                context.getString(R.string.proximity_audios),
                context.getString(R.string.proximity_audios_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("audio", "proximity", "sensor", "earpiece")));

        catalog.add(new SearchableFeature("alertsticker",
                context.getString(R.string.enable_confirmation_to_send_sticker),
                context.getString(R.string.enable_confirmation_to_send_sticker_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("sticker", "confirmation", "alert")));

        catalog.add(new SearchableFeature("remove_sticker_white_outline",
                context.getString(R.string.remove_sticker_white_outline),
                context.getString(R.string.remove_sticker_white_outline_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("sticker", "outline", "white")));

        catalog.add(new SearchableFeature("scheduled_messages",
                context.getString(R.string.scheduled_messages),
                context.getString(R.string.scheduled_messages_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("schedule", "messages", "timer", "send")));

        catalog.add(new SearchableFeature("chat_analytics",
                context.getString(R.string.chat_analytics),
                context.getString(R.string.chat_analytics_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("analytics", "statistics", "frequency", "chat")));

        catalog.add(new SearchableFeature("lockedchats_enhancer",
                context.getString(R.string.locked_chats),
                context.getString(R.string.locked_chats_sum),
                SearchableFeature.Category.CONVERSATION,
                SearchableFeature.FragmentType.CONVERSATION,
                null,
                Arrays.asList("locked", "chats", "biometric", "security")));
    }

    private static void addStatusFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("antirevokestatus",
                context.getString(R.string.antirevokestatus),
                context.getString(R.string.antirevokestatus_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("anti", "delete", "status")));

        catalog.add(new SearchableFeature("statusdowload",
                context.getString(R.string.statusdowload),
                context.getString(R.string.statusdowload_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("status", "download", "save")));

        catalog.add(new SearchableFeature("autonext_status",
                context.getString(R.string.disable_auto_status),
                context.getString(R.string.disable_auto_status_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("auto", "status", "skip", "next")));

        catalog.add(new SearchableFeature("igstatus",
                context.getString(R.string.igstatus_on_home_screen),
                context.getString(R.string.igstatus_on_home_screen_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("instagram", "status", "ig", "stories")));

        catalog.add(new SearchableFeature("toast_viewed_status",
                context.getString(R.string.toast_on_viewed_status),
                context.getString(R.string.toast_on_viewed_status_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("toast", "viewed", "status", "notification")));

        catalog.add(new SearchableFeature("copystatus",
                context.getString(R.string.enable_copy_status),
                context.getString(R.string.enable_copy_status_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("copy", "status", "caption")));

        catalog.add(new SearchableFeature("status_style",
                context.getString(R.string.style_of_stories_status),
                context.getString(R.string.style_of_stories_status_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("status", "style", "stories")));

        catalog.add(new SearchableFeature("oldstatus",
                context.getString(R.string.old_statuses),
                context.getString(R.string.old_statuses_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("old", "status", "vertical")));

        catalog.add(new SearchableFeature("statuscomposer",
                context.getString(R.string.custom_colors_for_text_status),
                context.getString(R.string.custom_colors_for_text_status_sum),
                SearchableFeature.Category.STATUS,
                SearchableFeature.FragmentType.STATUS,
                null,
                Arrays.asList("status", "composer", "colors", "text")));
    }

    private static void addPrivacyFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("ghostmode",
                context.getString(R.string.ghostmode),
                context.getString(R.string.ghostmode_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("ghost", "mode", "invisible")));

        catalog.add(new SearchableFeature("ghostmode_t",
                context.getString(R.string.hide_typing_status),
                context.getString(R.string.hide_typing_status_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("ghost", "typing", "hide")));

        catalog.add(new SearchableFeature("ghostmode_r",
                context.getString(R.string.hide_recording_status),
                context.getString(R.string.hide_recording_status_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("ghost", "recording", "audio")));

        catalog.add(new SearchableFeature("freezelastseen",
                context.getString(R.string.freezelastseen),
                context.getString(R.string.freezelastseen_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("freeze", "last seen")));

        catalog.add(new SearchableFeature("always_online",
                context.getString(R.string.always_online),
                context.getString(R.string.always_online_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("always", "online", "status")));

        catalog.add(new SearchableFeature("showonlinetext",
                context.getString(R.string.showonlinetext),
                context.getString(R.string.showonlinetext_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("online", "last seen", "text")));

        catalog.add(new SearchableFeature("dotonline",
                context.getString(R.string.dotonline),
                context.getString(R.string.dotonline_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("online", "dot", "green")));

        catalog.add(new SearchableFeature("antirevoke",
                context.getString(R.string.antirevoke),
                context.getString(R.string.antirevoke_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("anti", "revoke", "delete", "deleted")));

        catalog.add(new SearchableFeature("antidisappearing",
                context.getString(R.string.antidisappearing),
                context.getString(R.string.antidisappearing_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("anti", "disappearing", "temporary", "messages")));

        catalog.add(new SearchableFeature("revokeallmessages",
                context.getString(R.string.delete_for_everyone_all_messages),
                context.getString(R.string.delete_for_everyone_all_messages_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("delete", "everyone", "limit", "revoke")));

        catalog.add(new SearchableFeature("seentick",
                context.getString(R.string.seentick),
                null,
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("blue", "tick", "button", "mark", "read")));

        catalog.add(new SearchableFeature("blueonreply",
                context.getString(R.string.blue_ticks_after_reply),
                context.getString(R.string.blue_ticks_after_reply_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("blue", "tick", "reply")));

        catalog.add(new SearchableFeature("hideread",
                context.getString(R.string.hide_blue_ticks),
                context.getString(R.string.hide_blue_ticks_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("hide", "read", "blue", "ticks")));

        catalog.add(new SearchableFeature("hideaudioseen",
                context.getString(R.string.hide_voice_note_blue_ticks),
                context.getString(R.string.hide_voice_note_blue_ticks_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("audio", "seen", "hide", "mic")));

        catalog.add(new SearchableFeature("hideonceseen",
                context.getString(R.string.hide_view_once_opened),
                context.getString(R.string.hide_view_once_opened_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("view", "once", "seen", "hide")));

        catalog.add(new SearchableFeature("hideread_group",
                context.getString(R.string.hide_read_ticks_in_groups),
                context.getString(R.string.hide_read_ticks_in_groups_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("hide", "read", "group", "ticks")));

        catalog.add(new SearchableFeature("hidestatusview",
                context.getString(R.string.hideseen),
                context.getString(R.string.hideseen_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("status", "view", "hide", "anonymous")));

        catalog.add(new SearchableFeature("hidereceipt",
                context.getString(R.string.hide_second_tick),
                context.getString(R.string.hide_second_tick_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("hide", "delivered", "receipt", "second tick")));

        catalog.add(new SearchableFeature("typingprivacy",
                context.getString(R.string.typingprivacy),
                context.getString(R.string.typingprivacy_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("typing", "privacy", "hide")));

        catalog.add(new SearchableFeature("simulate_typing",
                context.getString(R.string.simulate_typing),
                context.getString(R.string.simulate_typing_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("simulate", "typing")));

        catalog.add(new SearchableFeature("simulate_recording",
                context.getString(R.string.simulate_recording),
                context.getString(R.string.simulate_recording_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("simulate", "recording", "audio")));

        catalog.add(new SearchableFeature("showonline",
                context.getString(R.string.show_toast_on_contact_online),
                context.getString(R.string.show_toast_on_contact_online_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("toast", "online", "notification")));

        catalog.add(new SearchableFeature("toastdeleted",
                context.getString(R.string.toast_on_delete),
                context.getString(R.string.toast_on_delete_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("toast", "deleted", "notification")));

        catalog.add(new SearchableFeature("toast_viewed_message",
                context.getString(R.string.toast_on_viewed_message),
                context.getString(R.string.toast_on_viewed_message_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("toast", "viewed", "read", "notification")));

        catalog.add(new SearchableFeature("dndmode",
                context.getString(R.string.dndmode),
                context.getString(R.string.dndmode_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("dnd", "disable", "internet")));

        catalog.add(new SearchableFeature("hidechat",
                context.getString(R.string.hidechat),
                context.getString(R.string.hidechat_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("hide", "chat", "lock", "pin")));

        catalog.add(new SearchableFeature("verify_blocked_contact",
                context.getString(R.string.show_contact_added_status),
                context.getString(R.string.show_contact_added_status_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("added", "contact", "verify")));

        catalog.add(new SearchableFeature("biometric_lock",
                context.getString(R.string.biometric_lock),
                context.getString(R.string.biometric_lock_sum),
                SearchableFeature.Category.PRIVACY,
                SearchableFeature.FragmentType.PRIVACY,
                null,
                Arrays.asList("biometric", "lock", "fingerprint", "app")));
    }

    private static void addCallsFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("call_privacy",
                context.getString(R.string.call_privacy),
                null,
                SearchableFeature.Category.CALLS,
                SearchableFeature.FragmentType.CALLS,
                null,
                Arrays.asList("call", "blocker", "privacy")));

        catalog.add(new SearchableFeature("call_type",
                context.getString(R.string.call_type),
                null,
                SearchableFeature.Category.CALLS,
                SearchableFeature.FragmentType.CALLS,
                null,
                Arrays.asList("call", "rejection", "type", "internet")));

        catalog.add(new SearchableFeature("call_info",
                context.getString(R.string.show_caller_network_info),
                context.getString(R.string.show_caller_network_info_sum),
                SearchableFeature.Category.CALLS,
                SearchableFeature.FragmentType.CALLS,
                null,
                Arrays.asList("call", "network", "info", "connection")));

        catalog.add(new SearchableFeature("call_block_contacts",
                context.getString(R.string.contact_block_list),
                context.getString(R.string.contact_block_list_sum),
                SearchableFeature.Category.CALLS,
                SearchableFeature.FragmentType.CALLS,
                null,
                Arrays.asList("call", "blacklist", "block")));

        catalog.add(new SearchableFeature("call_white_contacts",
                context.getString(R.string.contact_white_list),
                context.getString(R.string.contact_white_list_sum),
                SearchableFeature.Category.CALLS,
                SearchableFeature.FragmentType.CALLS,
                null,
                Arrays.asList("call", "whitelist", "allow")));
    }

    private static void addCustomizationFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("amoled_dark_theme",
                context.getString(R.string.amoled_dark_theme),
                context.getString(R.string.amoled_dark_theme_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("amoled", "dark", "pure black", "theme")));

        catalog.add(new SearchableFeature("changecolor",
                context.getString(R.string.colors_customization),
                context.getString(R.string.colors_customization_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("colors", "customization", "theme")));

        catalog.add(new SearchableFeature("changecolor_mode",
                context.getString(R.string.changecolor_mode),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("monet", "material you", "color mode")));

        catalog.add(new SearchableFeature("primary_color",
                context.getString(R.string.primary_color),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("primary", "color")));

        catalog.add(new SearchableFeature("background_color",
                context.getString(R.string.background_color),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("background", "color")));

        catalog.add(new SearchableFeature("text_color",
                context.getString(R.string.text_color),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("text", "color")));

        catalog.add(new SearchableFeature("wallpaper",
                context.getString(R.string.wallpaper_in_home_screen),
                context.getString(R.string.wallpaper_in_home_screen_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("wallpaper", "background", "image")));

        catalog.add(new SearchableFeature("wallpaper_alpha",
                context.getString(R.string.wallpaper_transparency),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("wallpaper", "transparency", "alpha")));

        catalog.add(new SearchableFeature("wallpaper_alpha_toolbar",
                context.getString(R.string.toolbar_transparency),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("toolbar", "transparency", "alpha")));

        catalog.add(new SearchableFeature("wallpaper_alpha_navigation",
                context.getString(R.string.navigation_transparency),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("navigation", "transparency", "alpha")));

        catalog.add(new SearchableFeature("bubble_color",
                context.getString(R.string.change_bubble_colors),
                context.getString(R.string.change_blubble_color_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("bubble", "color", "chat")));

        catalog.add(new SearchableFeature("bubble_left",
                context.getString(R.string.bubble_left),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("bubble", "left", "incoming")));

        catalog.add(new SearchableFeature("bubble_right",
                context.getString(R.string.bubble_right),
                null,
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("bubble", "right", "outgoing")));

        catalog.add(new SearchableFeature("custom_filters",
                context.getString(R.string.custom_appearance),
                context.getString(R.string.custom_filters_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("custom", "appearance", "filters", "css")));

        catalog.add(new SearchableFeature("filter_items",
                context.getString(R.string.filter_items_by_id),
                context.getString(R.string.filter_items_by_id_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("filter", "items", "id")));

        catalog.add(new SearchableFeature("css_theme",
                context.getString(R.string.custom_theme_css),
                context.getString(R.string.custom_theme_css_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("css", "theme", "style", "custom")));

        catalog.add(new SearchableFeature("change_dpi",
                context.getString(R.string.change_default_dpi),
                context.getString(R.string.change_default_dpi_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("dpi", "density", "scale")));

        catalog.add(new SearchableFeature("folder_theme",
                context.getString(R.string.theme_manager),
                context.getString(R.string.manage_theme_sum),
                SearchableFeature.Category.CUSTOMIZATION,
                SearchableFeature.FragmentType.CUSTOMIZATION,
                null,
                Arrays.asList("theme", "manager", "import", "export")));
    }

    private static void addMediaFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("download_local",
                context.getString(R.string.local_download),
                context.getString(R.string.local_download_sum),
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("download", "folder", "directory", "storage", "path", "local")));

        catalog.add(new SearchableFeature("downloadstatus",
                context.getString(R.string.auto_download_media),
                context.getString(R.string.auto_download_media_sum),
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("download", "status", "auto", "media")));

        catalog.add(new SearchableFeature("downloadviewonce",
                context.getString(R.string.unlimited_view_once_media),
                context.getString(R.string.unlimited_view_once_media_sum),
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("download", "view once", "unlimited")));

        catalog.add(new SearchableFeature("downloadprofile",
                context.getString(R.string.download_profile_pictures),
                context.getString(R.string.download_profile_pictures_sum),
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("download", "profile", "picture", "photo")));

        catalog.add(new SearchableFeature("videoquality",
                context.getString(R.string.send_high_quality_video),
                context.getString(R.string.send_high_quality_video_sum),
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("video", "quality", "hd", "high")));

        catalog.add(new SearchableFeature("video_limit_size",
                context.getString(R.string.increase_video_size_limit),
                context.getString(R.string.increase_video_size_limit_sum),
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("video", "size", "limit", "mb", "200mb")));

        catalog.add(new SearchableFeature("imagequality",
                context.getString(R.string.send_high_quality_photo),
                context.getString(R.string.send_high_quality_photo_sum),
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("image", "photo", "quality", "hd", "uncompressed")));

        catalog.add(new SearchableFeature("audio_type",
                context.getString(R.string.audio_as_voice),
                null,
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("audio", "voice", "note", "send")));

        catalog.add(new SearchableFeature("media_preview",
                context.getString(R.string.highres_media_preview),
                context.getString(R.string.highres_media_preview_sum),
                SearchableFeature.Category.MEDIA,
                SearchableFeature.FragmentType.MEDIA,
                null,
                Arrays.asList("media", "preview", "highres", "quality")));
    }

    private static void addMiscFeatures(Context context, List<SearchableFeature> catalog) {
        catalog.add(new SearchableFeature("bootloader_spoofer",
                context.getString(R.string.spoof_bootloader),
                context.getString(R.string.spoof_bootloader_sum),
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("bootloader", "spoofer", "lock", "bypass", "safety")));

        catalog.add(new SearchableFeature("bootloader_spoofer_custom",
                context.getString(R.string.spoof_keybox),
                context.getString(R.string.spoof_keybox_sum),
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("keybox", "attestation", "custom", "certificates")));

        catalog.add(new SearchableFeature("bootloader_spoofer_xml",
                context.getString(R.string.custom_keybox_file),
                context.getString(R.string.custom_keybox_file_sum),
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("keybox", "xml", "file", "select")));

        catalog.add(new SearchableFeature("tasker",
                context.getString(R.string.tasker_integration),
                context.getString(R.string.tasker_integration_sum),
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("tasker", "automation", "intent", "integration")));

        catalog.add(new SearchableFeature("disable_expiration",
                context.getString(R.string.disable_whatsapp_expiration),
                context.getString(R.string.disable_whatsapp_expiration_sum),
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("expiration", "version", "disable")));

        catalog.add(new SearchableFeature("force_restore_backup_feature",
                context.getString(R.string.force_restore_backup),
                context.getString(R.string.force_restore_backup_summary),
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("backup", "restore", "force")));

        catalog.add(new SearchableFeature("force_english",
                context.getString(R.string.force_english),
                null,
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("english", "language", "force")));

        catalog.add(new SearchableFeature("disable_ads",
                context.getString(R.string.disable_ads),
                context.getString(R.string.disable_ads_sum),
                SearchableFeature.Category.MISC,
                SearchableFeature.FragmentType.MISC,
                null,
                Arrays.asList("ads", "disable", "admob")));
    }

    private static void addHomeActions(Context context, List<SearchableFeature> catalog) {
        // Settings & About Actions
        catalog.add(new SearchableFeature("export_config",
                context.getString(R.string.export_settings),
                context.getString(R.string.backup_settings),
                SearchableFeature.Category.SETTINGS,
                SearchableFeature.FragmentType.SETTINGS_ABOUT,
                null,
                Arrays.asList("export", "backup", "settings", "config")));

        catalog.add(new SearchableFeature("import_config",
                context.getString(R.string.import_settings),
                context.getString(R.string.backup_settings),
                SearchableFeature.Category.SETTINGS,
                SearchableFeature.FragmentType.SETTINGS_ABOUT,
                null,
                Arrays.asList("import", "restore", "settings", "config")));

        catalog.add(new SearchableFeature("reset_config",
                context.getString(R.string.reset_settings),
                null,
                SearchableFeature.Category.SETTINGS,
                SearchableFeature.FragmentType.SETTINGS_ABOUT,
                null,
                Arrays.asList("reset", "settings", "clear")));

        catalog.add(new SearchableFeature("reboot_wpp",
                context.getString(R.string.restart_whatsapp),
                null,
                SearchableFeature.Category.SETTINGS,
                SearchableFeature.FragmentType.SETTINGS_ABOUT,
                null,
                Arrays.asList("restart", "reboot", "whatsapp", "refresh")));
    }
}
