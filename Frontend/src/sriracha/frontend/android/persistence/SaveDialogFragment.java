package sriracha.frontend.android.persistence;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;

/**
 * The dialog box for saving a circuit or a netlist to the filesystem.
 * Must be called from MainActivity, since the callback that talks to the storage is located there.
 */
public class SaveDialogFragment extends DialogFragment
{
    public final static int SAVE_CIRCUIT = 0;
    public final static int SAVE_NETLIST = 1;

    private int dialogId;

    public SaveDialogFragment(int dialogId)
    {
        this.dialogId = dialogId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getDialog().setTitle("Save As...");
        View view = inflater.inflate(R.layout.save_file_dialog, container, false);

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                save();
            }
        });
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        return view;
    }

    private void save()
    {
        String fileName = ((TextView)getDialog().findViewById(R.id.save_file_name)).getText().toString();
        if (fileName.isEmpty())
        {
            ((MainActivity)getActivity()).showToast("Please enter a file name");
            return;
        }

        if (((MainActivity)getActivity()).save(fileName, dialogId))
        {
            dismiss();
            ((MainActivity)getActivity()).showToast("Saved");
        }
    }
}
