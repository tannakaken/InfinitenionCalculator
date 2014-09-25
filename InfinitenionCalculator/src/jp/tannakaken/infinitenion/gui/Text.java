package jp.tannakaken.infinitenion.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.tannakaken.infinitenion.R;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
/**
 * ���܂��܂ȃe�L�X�g��\�����邽�߂�Activity�B
 * @author tannakaken
 *
 */
public class Text extends Activity {
	/**
	 * �ǂ̃e�L�X�g��\�����邩��\��{@link TextResource}�B
	 */
	private static TextResource mTextResource;
	
	/**
	 * ���̊��ł̉��s�B
	 */
	private static final String NEW_LINE = System.getProperty("line.separator"); 
	/**
	 * ����Activity���ǂ̃e�L�X�g��\�����邩�����߂�B<br>
	 * {@link onCreate}����O�ɕK�����̃��\�b�h�����邱�ƁB
	 * @param aTextResource �\������e�L�X�g��\��{@link TextResource}�B
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
		try {
			switch (mTextResource) {
			case ABOUT_THIS_APP:
				setTitle(getText(R.string.about_this_app_label));
				if (getResources().getConfiguration().locale.getLanguage().equals("ja")) {
					tReader = new BufferedReader(new InputStreamReader(tManager.open("about_this_app_ja.txt"),
							"MS932"));
				} else {
					tReader = new BufferedReader(new InputStreamReader(tManager.open("about_this_app_en.txt")));
				}
				break;
			case ABOUT_HYPERCOMPLEX_NUMBES:
				setTitle(getText(R.string.about_hypercomplex_number_label));
				tReader = new BufferedReader(new InputStreamReader(tManager.open("about_hypercomplex_number.txt"),
						"MS932"));
				break;
			case ABOUT_REVERSE_POLAND:
				setTitle(getText(R.string.about_reverse_poland_label));
				tReader = new BufferedReader(new InputStreamReader(tManager.open("about_reverse_poland.txt"),
						"MS932"));
				break;
			case HELP:
				setTitle(getText(R.string.help_label));
				if (getResources().getConfiguration().locale.getLanguage().equals("ja")) {
					tReader = new BufferedReader(new InputStreamReader(tManager.open("help_ja.txt"), "UTF-8"));
				} else {
					tReader = new BufferedReader(new InputStreamReader(tManager.open("help_en.txt"), "UTF-8"));
				}
				
				break;
			case PROBLEM:
				setTitle(getText(R.string.problem_label));
				tReader = new BufferedReader(new InputStreamReader(tManager.open("problem.txt"), "UTF-8"));
				break;
			default:
				throw new IllegalStateException("Unknow text resource.");
			} 
			String tLine;
			while ((tLine = tReader.readLine()) != null) {
				tContent.append(tLine + NEW_LINE);
			}
		} catch (IOException e) {
			Toast.makeText(this, "�t�@�C���̓ǂݍ��݂Ɏ��s���܂���", Toast.LENGTH_SHORT).show();
		}
	}
}
