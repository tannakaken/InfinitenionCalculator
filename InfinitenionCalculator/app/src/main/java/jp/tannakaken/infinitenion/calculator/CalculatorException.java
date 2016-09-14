package jp.tannakaken.infinitenion.calculator;

import android.content.Context;

/**
 * 計算の途中で発生する例外の親クラス。
 * @author tannakaken
 *
 */
public abstract class CalculatorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 出力するエラーメッセージを取得するための、{@link Context}。
	 */
	private static Context mContext;
	/**
	 * 必ず{@link jp.tannakaken.infinitenion.gui.MainActivity}の起動時にこのメソッドで初期化すること。。
	 * @param aContext 出力するエラーメッセージを取得するための、{@link Context}。
	 */
	public static void setContext(final Context aContext) {
		mContext = aContext;
	}
	/**
	 * 
	 * @param aMessageId 表示するエラーメッセージを取得するためのリソースID。
	 */
	public CalculatorException(final int aMessageId) {
		super(mContext.getString(aMessageId));
	}
	/**
	 * 
	 * @param aMessageId 表示するエラーメッセージを取得するためのリソースID。
	 * @param aCause エラーの原因となったトークン。
	 */
	public CalculatorException(final int aMessageId, final Object aCause) {
		super(mContext.getString(aMessageId) + ":" + aCause);
	}
	
}
