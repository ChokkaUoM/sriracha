package sriracha.frontend.android.persistence;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;

public class SaveDialogFragment extends DialogFragment
{
    public static SaveDialogFragment newInstance()
    {
        return new SaveDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().setTitle("Save Circuit");
        View view = inflater.inflate(R.layout.save_file_dialog, container, false);

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                saveCircuit();
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

    private void saveCircuit()
    {
        String fileName = ((TextView)getDialog().findViewById(R.id.save_file_name)).getText().toString();
        if (fileName.isEmpty())
        {
            ((MainActivity)getActivity()).showToast("Please enter a file name");
            return;
        }

        if (((MainActivity)getActivity()).saveCircuit(fileName))
        {
            dismiss();
            ((MainActivity)getActivity()).showToast("Saved");
        }
    }
}
