package jp.tannakaken.infinitenion.gui;

import jp.tannakaken.infinitenion.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * ������O����html�������{�����邽�߂�{@link activity}�B
 * @author tannakaken
 *
 */
public class Web extends Activity {
	/**
	 * ����Acticiry�ŕ\������html������\��{@link WebResource}�B
	 */
	private static WebResource mWebResource;
	/**
	 * ����Activity���ǂ�html������\�����邩�����߂�B<br>
	 * {@link onCreate}����O�ɕK�����̃��\�b�h�����邱�ƁB
	 * @param aWebResource �\������e�L�X�g��\��{@link WebResource}�B
	 */
	public static final void setResource(final WebResource aWebResource) {
		mWebResource = aWebResource;
	}
	
	@Override
	protected final void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		setContentView(R.layout.web);
		WebView tContent = (WebView) findViewById(R.id.web_content);
		switch (mWebResource) {
		case ABOUT_AUTHOR:
			setTitle(getText(R.string.about_author));
			if (getResources().getConfiguration().locale.getLanguage().equals("ja")) {
				tContent.loadUrl("file:///android_asset/about_author_ja.html");
			} else {
				tContent.loadUrl("file:///android.asset/about_author_en.html");
			}
			return;
		case STORY:
			setTitle(getText(R.string.story_label));
			tContent.loadUrl("http://tannakaken.utun.net/quarternion.html");
			return;
		default:
			throw new IllegalStateException("Unknown webResource exists.");
		}
	}
	
}
