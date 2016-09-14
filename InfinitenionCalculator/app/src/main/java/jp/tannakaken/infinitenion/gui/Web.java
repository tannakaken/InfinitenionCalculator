package jp.tannakaken.infinitenion.gui;

import jp.tannakaken.infinitenion.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * 内部や外部のhtml文書を閲覧するための{@link activity}。
 * @author tannakaken
 *
 */
public class Web extends Activity {
	/**
	 * このActiciryで表示するhtml文書を表す{@link WebResource}。
	 */
	private static WebResource mWebResource;
	/**
	 * このActivityがどのhtml文書を表示するかを決める。<br>
	 * {@link onCreate}する前に必ずこのメソッドをすること。
	 * @param aWebResource 表示するテキストを表す{@link WebResource}。
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
			tContent.loadUrl("file:///android_asset/story.html");
			return;
		default:
			throw new IllegalStateException("Unknown webResource exists.");
		}
	}
	
}
