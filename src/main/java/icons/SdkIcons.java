// Copyright 2000-2021 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package icons;

import javax.swing.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SdkIcons {

    public static final Icon SNAP_ICON = IconLoader.getIcon("/icons/snap/snapshotGutter_dark.svg", SdkIcons.class);
    public static final Icon DIFF_ICON = IconLoader.getIcon("/icons/diff/diff_dark.svg", SdkIcons.class);
    public static final Icon DELETE_ICON = AllIcons.Actions.GC;
    public static final Icon CLEAR_ICON = AllIcons.Debugger.Db_invalid_breakpoint;
    public static final Icon VIEW_NODES_ICON = IconLoader.getIcon("/icons/view_saved_nodes/manageDataSources_dark.svg", SdkIcons.class);
    public static final Icon DIFF_SCALED = IconLoader.getIcon("/icons/diff/diff_dark_scaled.svg", SdkIcons.class);

}
