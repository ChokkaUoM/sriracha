package sriracha.frontend.android.persistence;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.R;

import java.io.*;

/**
 * The dialog box for loading a circuit from the filesystem.
 * Must be called from MainActivity, since the callback that talks to the storage is located there.
 */
public class LoadDialogFragment extends DialogFragment
{
    public static LoadDialogFragment newInstance()
    {
        return new LoadDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().setTitle("Load Circuit");
        View view = inflater.inflate(R.layout.load_file_dialog, container, false);

        try
        {
            String[] files = new Storage(getActivity()).list(".occ");
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, files);

            ListView filesList = (ListView) view.findViewById(R.id.load_file_list);
            filesList.setAdapter(adapter);
            filesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
                {
                    loadCircuit(adapter.getItem(position));
                }
            });

            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dismiss();
                }
            });
        }
        catch (IOException e)
        {
            Toast toast = Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            dismiss();
        }

        return view;
    }

    private void loadCircuit(String fileName)
    {
        if (((MainActivity) getActivity()).loadCircuit(fileName))
            dismiss();
    }
}
