package sriracha.frontend.android.persistence;

import android.content.*;
import android.os.*;

import java.io.*;

public class Storage
{
    private Context context;

    public Storage(Context context)
    {
        this.context = context;
    }

    public String[] list() throws IOException
    {
        ensureCanRead();

        File file = context.getExternalFilesDir(null);
        String[] files = file.list();
        return files;
    }

    public void load(String fileName, Serialization serialization) throws IOException, ClassNotFoundException
    {
        ensureCanRead();

        FileInputStream fileInputStream = null;
        ObjectInputStream in = null;
        try
        {
            File file = new File(context.getExternalFilesDir(null), fileName);
            fileInputStream = new FileInputStream(file);
            in = new ObjectInputStream(fileInputStream);
            serialization.deserialize(in);
        }
        finally
        {
            if (in != null)
                in.close();
            if (fileInputStream != null)
                fileInputStream.close();
        }
    }

    public void save(String fileName, Serialization serialization) throws IOException
    {
        ensureCanWrite();

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream out = null;
        try
        {
            File file = new File(context.getExternalFilesDir(null), fileName);
            fileOutputStream = new FileOutputStream(file);
            out = new ObjectOutputStream(fileOutputStream);
            serialization.serialize(out);
        }
        finally
        {
            if (out != null)
                out.close();
            if (fileOutputStream != null)
                fileOutputStream.close();
        }
    }

    private void ensureCanRead() throws IOException
    {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            throw new IOException("Cannot read from storage");
    }

    private void ensureCanWrite() throws IOException
    {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state))
            throw new IOException("Cannot write to storage");
    }
}
