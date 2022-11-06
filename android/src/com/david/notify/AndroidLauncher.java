package com.david.notify;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AndroidLauncher extends AndroidFragmentApplication {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
//		inflater.inflate(R.layout.main_activity_with_david,container);
		// return the GLSurfaceView on which libgdx is drawing game stuff
		return initializeForView(new DavidRenderer());
	}

//	@Override
//	protected void onCreate (Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		initialize(new DavidRenderer(), config);
//	}
}
