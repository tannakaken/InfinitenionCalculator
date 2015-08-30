package jp.tannakaken.infinitenion.gui;

import jp.tannakaken.infinitenion.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 各種アバウトの選択画面。
 * @author tannakaken
 *
 */
public class About extends Activity implements OnClickListener {
	@Override
	protected final void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		setContentView(R.layout.about);
		
		findViewById(R.id.about_this_app).setOnClickListener(this);
		findViewById(R.id.about_hypercomplex_number).setOnClickListener(this);
		findViewById(R.id.about_reverse_poland).setOnClickListener(this);
		findViewById(R.id.about_author).setOnClickListener(this);
	}

	@Override
	public final void onClick(final View aView) {
		Intent i;
		switch (aView.getId()) {
		case R.id.about_this_app:
			Text.setResource(TextResource.ABOUT_THIS_APP);
			i = new Intent(this, Text.class);
			startActivity(i);
			return;
		case R.id.about_hypercomplex_number:
			Text.setResource(TextResource.ABOUT_HYPERCOMPLEX_NUMBES);
			i = new Intent(this, Text.class);
			startActivity(i);
			return;
		case R.id.about_reverse_poland:
			Text.setResource(TextResource.ABOUT_REVERSE_POLAND);
			i = new Intent(this, Text.class);
			startActivity(i);
			return;
		case R.id.about_author:
			Web.setResource(WebResource.ABOUT_AUTHOR);
			i = new Intent(this, Web.class);
			startActivity(i);
			return;
		default:
			throw new IllegalStateException("Unknown wiew exists.");
		}
		
	}
}
