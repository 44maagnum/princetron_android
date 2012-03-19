/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package princeTron.UserInterface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Tron: a simple game that everyone can enjoy.
 * 
 * This is an implementation of the game Tron
 * 
 */
public class PrinceTron extends Activity {

	private ArenaView mArenaView;

	/**
	 * Called when Activity is first created. Turns off the title bar, sets up
	 * the content views, and fires up the ArenaView.
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.arena_layout);

		mArenaView = (ArenaView) findViewById(R.id.arena);
		mArenaView.setTextView((TextView) findViewById(R.id.text));

		mArenaView.setMode(ArenaView.READY);

	}




}