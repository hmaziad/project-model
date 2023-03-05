package org.armadillo.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextConstants {
    public static final String SHOW_SNAPS_HERE = "Show snaps here";
    public static final String DELETE_SAVED_NODE = "Are you sure you want to delete the following saved session(s): %n%s";
    public static final String DELETE_SAVED_NODES = "Are you sure you want to delete all sessions?";
    public static final String CURRENT_SESSION = "Current Session";
    public static final String DELETE_SESSION_BELOW = "Delete session from storage";
    public static final String CLEAR_SESSION_BELOW = "Clear session";
    public static final String FLOWS_TITLE = "Flows";
    public static final String OPEN_DIFF_WINDOW = "Open Diff";
    public static final String OPEN_SETTINGS_WINDOW = "Open Settings";
    public static final String SAVE_DEBUGGER_SESSION = "Save Debugger Session (Ctrl-A-S)";
    public static final String COMPARING_SESSIONS = "Comparing Sessions";
    public static final String DELETE_SESSION = "Armadillo: Delete Session";
    public static final String EXPORT_SESSION = "Armadillo: Export Session(s)";
    public static final String NODE_DATE_FORMAT = "yyyy-MM-dd_hh-mm-ss.SSS";
    public static final String HUMAN_DATE_FORMAT = "yyyy-MMM-dd_hh-mm-ss";
    public static final String GENERATED_SESSION_NAME = "Snap_%s_%s";
    public static final String DIFF_NODES = "Armadillo: Diff Sessions";
    public static final String SETTINGS_VIEW_TITLE = "Armadillo: Manage Saved Sessions";
    public static final String FLOW_VIEW_TITLE = "Armadillo: View Flows";
    public static final String METHOD_BLOCK_VIEW_TITLE = "Armadillo: View Method Block";
    public static final String EMPTY_STRING = "";
    public static final String REGISTER_PLUGIN = "Please register Armadillo plugin.";
    public static final String GET_PAID_VERSION =
        "Reached a limit of 8 saved snapshots. Please upgrade to the paid version for unlimited snapshots.";
    public static final String GET_PAID_VERSION_IMPORT =
        "Combined existing and to be imported snapshots are 8 or more. Please upgrade to the paid version for unlimited snapshots.";

}
