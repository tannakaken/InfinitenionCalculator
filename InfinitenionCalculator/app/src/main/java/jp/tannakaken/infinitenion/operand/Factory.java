package jp.tannakaken.infinitenion.operand;


import java.util.ArrayDeque;
import java.util.Deque;

import android.content.Context;
import android.os.AsyncTask;
import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
import jp.tannakaken.infinitenion.calculator.CalculatorParseException;

/**
 * トークンを検査して、もし受容可能なら{@link Operand}を作る工程を一本化するための、Factory Methodパターン。<br>
 * 計算機の計算モードを{@link android.preference.PreferenceManager}から取得するために、
 * プログラムの開始すぐに、{@link Factory#setContext(Context)}をすること。<br>
 * それを怠った場合の挙動は一切保証しない。
 * 
 * @author tannakaken
 * @see
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">
 * http://en.wikipedia.org/wiki/Factory_method_pattern</a><br/>
 *
 */
public abstract class Factory {
	/**
	 * 現在のモードを取得するための{@link Context}。
	 */
	private static Context mContext;
	/**
	 * Factoryのproductに{@link Context}を注入しなくてはいけなくなったら、
	 * それらのクラスにクラスメソッドとしてsetterを定義して、ここで呼び出す。
	 * @param aContext 現在の{@link Context}。
	 */
	public static void setContext(final Context aContext) {
		mContext = aContext;
		Constant.setContext(aContext);
	}
	/**
	 * プログラムの開始すぐに、{@link Factory#setContext(Context)}をすること。<br>
	 * それを怠った場合の挙動は一切保証しない。
	 * @return 現在のモードを取得するための{@link Context}。
	 */
	static Context getContext() {
		return mContext;
	}
	/**
	 * 作った{@link Operand}を保管していくために、{@link Deque}をスタックとして使う。
	 */
	private static Deque<Operand> mStack = new ArrayDeque<>();
	/**
	 *
	 *
	 * @return {@link Operand}を貯蔵するためのスタック。 
	 */
	static Deque<Operand> getStack() {
		return mStack;
	}
	/**
	 * 計算を実行している非同期タスク。
	 */
	private static AsyncTask<String, Void, String> mTask;
	/**
	 * 
	 * @param aTask 計算を実行している非同期タスク。
	 */
	public static void setTask(final AsyncTask<String, Void, String> aTask) {
		mTask = aTask;
	}
	/**
	 * 
	 * @return 非同期タスクがキャンセルされると、このメソッドの返り血がtrueになる。
	 */
	public static boolean isCanceled() {
		return mTask.isCancelled();
	}
	/**
	 * 必ず{@link Factory#calc}する前に、このメソッドで検査すること。<br>
	 * もし、それがこのFactoryによって、{@link Operand}に変換できるなら、
	 * このメソッドによって、トークンが切り出されてFactoryが準備状態に入り、
	 * トークンを切りだされた残りの文字列が出力され、
	 * {@link Factory#calc()}によって、{@link Operand}が生成され、
	 * {@link Factory#mStack}に追加される。<br>
	 * それを守らなかった場合の挙動は一切保証しない。
	 * @param aInput トークンを切り出そうとする文字列。
	 * @return 残りの文字列。切り出せなかった時はnull
	 * @throws CalculatorParseException 現在のモードでは仕えないトークンが使用されることによる例外。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	public abstract String getReady(String aInput)
			throws CalculatorParseException, BackgroundProcessCancelledException;
	
	/**
	 * 必ずこのメソッドを使う前に、{@link Factory#getReady(String)}で検査すること。<br>
	 * もし、それがこのFactoryによって、{@link Operand}に変換できるなら、
	 * それにより、Factoryが準備状態に入り、
	 * このメソッドによって、トークンが表す{@link Operand}、
	 * もしくはトークンの表すオペレータを{@link Factory#mStack}から取り出した{@link Operand}に適用した{@link Operand}が生成され、
	 * {@link Factory#mStack}に追加される。<br>
	 * それを守らなかった場合の挙動は一切保証しない。
	 * @throws CalculatingException オペレータの要求する文法の違反による例外。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	public abstract void calc() throws CalculatingException, BackgroundProcessCancelledException;
	
	/**
	 * 
	 * @return スタックに格納された計算結果を文字列にする。
	 */
	public static String getResult() {
		StringBuilder tResult = new StringBuilder();
		for (Operand tOperand : mStack) {
			tResult.insert(0, ", " + tOperand);
		}
		return tResult.toString().replaceFirst(",", "").trim();
	}
	/**
	 * スタックをクリアする。
	 */
	public static void clearStack() {
		mStack.clear();
	}
}
