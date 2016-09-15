package jp.tannakaken.infinitenion.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.tannakaken.infinitenion.R;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
/**
 * さまざまなテキストを表示するためのActivity。
 * @author tannakaken
 *
 */
public class Text extends Activity {
	/**
	 * どのテキストを表示するかを表す{@link TextResource}。
	 */
	private static TextResource mTextResource;
	
	/**
	 * この環境での改行。
	 */
	private static final String NEW_LINE = System.getProperty("line.separator"); 
	/**
	 * このActivityがどのテキストを表示するかを決める。<br>
	 * {@link this.onCreate}する前に必ずこのメソッドをすること。
	 * @param aTextResource 表示するテキストを表す{@link TextResource}。
	 */
	public static void setResource(final TextResource aTextResource) {
		mTextResource = aTextResource;
	}
	
	@Override
	protected final void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		setContentView(R.layout.text);
		TextView tContent = (TextView) findViewById(R.id.text_content);
		AssetManager tManager = getResources().getAssets();
		BufferedReader tReader;
		String tLanguage;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			tLanguage = getResources().getConfiguration().getLocales().get(0).getLanguage();
		} else {
			tLanguage = getResources().getConfiguration().locale.getLanguage();
		}
		try {
			switch (mTextResource) {
			case ABOUT_THIS_APP:
				setTitle(getText(R.string.about_this_app_label));
				if (tLanguage.equals("ja")) {
					tReader = new BufferedReader(new InputStreamReader(tManager.open("about_this_app_ja.txt"),
							"UTF-8"));
				} else {
					tReader = new BufferedReader(new InputStreamReader(tManager.open("about_this_app_en.txt")));
				}
				break;
			case ABOUT_HYPERCOMPLEX_NUMBES:
				setTitle(getText(R.string.about_hypercomplex_number_label));
				tReader = new BufferedReader(new InputStreamReader(tManager.open("about_hypercomplex_number.txt"),
						"UTF-8"));
				break;
			case ABOUT_REVERSE_POLAND:
				setTitle(getText(R.string.about_reverse_poland_label));
				tReader = new BufferedReader(new InputStreamReader(tManager.open("about_reverse_poland.txt"),
						"UTF-8"));
				break;
			case HELP:
				setTitle(getText(R.string.help_label));
				if (tLanguage.equals("ja")) {
					tReader = new BufferedReader(new InputStreamReader(tManager.open("help_ja.txt"), "UTF-8"));
				} else {
					tReader = new BufferedReader(new InputStreamReader(tManager.open("help_en.txt"), "UTF-8"));
				}
				
				break;
			case EXERCISE:
				setTitle(getText(R.string.exercise_label));
				tReader = new BufferedReader(new InputStreamReader(tManager.open("exercise.txt"), "UTF-8"));
				break;
			default:
				throw new IllegalStateException("Unknow text resource.");
			} 
			String tLine;
			while ((tLine = tReader.readLine()) != null) {
				tContent.append(tLine + NEW_LINE);
			}
		} catch (IOException e) {
			Toast.makeText(this, "ファイルの読み込みに失敗しました", Toast.LENGTH_SHORT).show();
		}
	}
}
