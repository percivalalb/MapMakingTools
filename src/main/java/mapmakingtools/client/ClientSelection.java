package mapmakingtools.client;

import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.worldeditor.EmptySelection;

public class ClientSelection {

    public static ISelection SELECTION = EmptySelection.INSTANCE;
    public static String LAST_COMMAND = null;
}
