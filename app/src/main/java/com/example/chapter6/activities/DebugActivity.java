package com.example.chapter6.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter6.R;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DebugActivity extends AppCompatActivity
{
	private static int REQUEST_CODE_STORAGE_PERMISSION = 1001;

	public static void launch(Activity activity)
	{
		Intent intent = new Intent(activity, DebugActivity.class);

		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);

		// get widgets
		final Button pathBtn = findViewById(R.id.debug_pathBtn);
		final TextView pathView = findViewById(R.id.debug_pathText);
		final Button permissionBtn = findViewById(R.id.debug_permissionBtn);

		// set callback
		pathBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String res =
						"===== Internal Private =====\n" + getInternalPrivatePath() +
						"===== External Private =====\n" + getExternalPrivatePath() +
						"===== External Public  =====\n" + getExternalPublicPath();
				pathView.setText(res);
			}
		});

		permissionBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// get permission state
				int state = ActivityCompat.checkSelfPermission(
						DebugActivity.this,
						Manifest.permission.WRITE_EXTERNAL_STORAGE);

				if (state == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(
							DebugActivity.this,
							"already granted",
							Toast.LENGTH_SHORT).show();

				} else {
					ActivityCompat.requestPermissions(
							DebugActivity.this,
							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							REQUEST_CODE_STORAGE_PERMISSION);
				}
			}
		});

	}

	private String getInternalPrivatePath()
	{
		Map<String, File> map = new LinkedHashMap<>();
		map.put("cacheDir", getCacheDir());
		map.put("filesDir", getFilesDir());
		map.put("customDir", getDir("custom", MODE_PRIVATE));
		return getCanonicalPath(map);
	}

	private String getExternalPrivatePath()
	{
		Map<String, File> map = new LinkedHashMap<>();
		map.put("cacheDir", getExternalCacheDir());
		map.put("filesDir", getExternalFilesDir(null));
		map.put("picturesDir", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
		return getCanonicalPath(map);
	}

	private String getExternalPublicPath()
	{
		Map<String, File> map = new LinkedHashMap<>();
		map.put("rootDir", Environment.getExternalStorageDirectory());
		map.put("picturesDir",
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
		return getCanonicalPath(map);
	}

	private String getCanonicalPath(Map<String, File> map)
	{
		StringBuilder ret = new StringBuilder();
		for (String name : map.keySet()) {
			try {
				ret.append(name).append(": ").append(map.get(name).getCanonicalPath()).append("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret.toString();
	}
}
