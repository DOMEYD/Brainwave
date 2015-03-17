package fr.iut.brainwave;

//import fr.iut.brainwave.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
/**
 * Permet d'afficher le menu d'aide
 * 
 * @author Robin, Chafik, Loïc, Cécile
 *
 */
public class AideActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aide);
	}
}
