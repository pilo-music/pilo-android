package app.pilo.android.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import app.pilo.android.db.AppDatabase;
import app.pilo.android.db.UserDao;
import app.pilo.android.models.User;

public class UserRepo {
    private static UserRepo instance;
    private UserDao userDao;

    public static UserRepo getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepo(context);
        }
        return instance;
    }

    public UserRepo(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        userDao = database.userDao();
    }


    public User get() {
        try {
            return new GetUserAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(User user) {
        AsyncTask.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        AsyncTask.execute(() -> userDao.insert(user));
    }

    public void delete(User user) {
        AsyncTask.execute(() -> userDao.insert(user));
    }


    @SuppressLint("StaticFieldLeak")
    private class GetUserAsyncTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... voids) {
            return userDao.get();
        }
    }
}
