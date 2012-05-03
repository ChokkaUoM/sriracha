package sriracha.frontend.android.persistence;

import android.content.*;
import android.os.*;

import java.io.*;
import java.lang.reflect.*;

/**
 * Uses the Android API to talk to the filesystem for saving and loading files.
 * Naturally, a whole lot of different exceptions may be thrown if the filesystem
 * disagrees with us for some reason, and it's the responsibility of the calling 
 * method to deal with these exceptions.
 */
public class Storage
{
    private Context context;

    public Storage(Context context)
    {
        this.context = context;
    }

    public String[] list(final String extensionFilter) throws IOException
    {
        ensureCanRead();

        File file = context.getExternalFilesDir(null);
        String[] files = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name)
            {
                return name.endsWith(extensionFilter);
            }
        });
        return files;
    }

    public void load(String fileName, Serialization serialization) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
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

    public void save(String fileName, String data) throws IOException
    {
        ensureCanWrite();

        FileOutputStream fileOutputStream = null;
        try
        {
            File file = new File(context.getExternalFilesDir(null), fileName);
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes(), 0, data.length());
        }
        finally
        {
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
