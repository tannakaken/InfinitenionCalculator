package jp.tannakaken.infinitenion.operand;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import android.content.Context;

/**
 * トークンから{@link Variable}を生成して登録するFactory。<br>
 * その際、トークンの大文字小文字は区別しない。<br>
 * トークンの検査をしてからインスタンスを構成する流れをFactory Methodパターンを利用して構成。<br>
 * 登録のしかたにFlyWeightパターンを使う。<br>
 * 一つしかインスタンスが必要ないので、Singletonパターンを使う。
 * 
 * @author tannakaken
 *　
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">http://en.wikipedia.org/wiki/Factory_method_pattern</a>
 * <a href="http://en.wikipedia.org/wiki/Flyweight_pattern">http://en.wikipedia.org/wiki/Flyweight_pattern</a>
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 *
 */
public final class VariableFactory extends Factory {
	/**
	 * この環境での改行。
	 */
	private static final String NEW_LINE = System.getProperty("line.separator"); 
	/**
	 * 一つしか必要ないので<a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton</a>。
	 * 
	 */
	private static VariableFactory mSingleton = new VariableFactory();
	
	/**
	 * {@link Variable}を格納しておくプール。
	 * {@link Variable}を表すトークン「XもしくはXn（nは正の整数)」の添え字による順序で整列させる。
	 */
	private static Map<String, Variable> mPool = new TreeMap<String, Variable>(new Comparator<String>() {
		@Override
		public int compare(final String aLeft, final String aRight) {
			int tLeftLength = aLeft.length();
			int tRightLength = aRight.length();
			if (tLeftLength > tRightLength) {
				return 1;
			} else if (tLeftLength < tRightLength) {
				return -1;
			} else {
				return aLeft.compareTo(aRight);
			}
		}
	});
	/**
	 * {@link Variable}のトークンの一般形をあらわす正規表現。
	 */
	private static String mRegex = "^X$|^X[1-9][0-9]*$";
	/**
	 * {@link Variable}が変数かどうかを判定する正規表現のパターン。
	 */
	private static Pattern mPattern = Pattern.compile(mRegex);
	/**
	 * {@link Variable}を作るための、受け入れ可能なトークン。
	 */
	private static String mToken;
	/**
	 * 外部からインスタンス化できないようにしておく。
	 */
	private VariableFactory() { }
	
	/**
	 * 
	 * @return {@link VariableFactory}の<a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton</a>。
	 * 
	 */
	public static VariableFactory getInstance() {
		return mSingleton;
	}
	@Override
	public boolean getReady(final String aToken) throws BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		if (mPattern.matcher(aToken).find()) {
			mToken = aToken;
			return true;
		}
		return false;
	}
	@Override
	public void calc() {
		Variable tVariable = mPool.get(mToken);
		if (tVariable == null) {
			tVariable = new Variable();
			mPool.put(mToken, tVariable);
		}
		getStack().push(tVariable);
	}
	
	/**
	 * 生成した{@link Variable}を全てクリアする。
	 */
	public void clearVariable() {
		mPool.clear();
	}
	
	/**
	 * 代入をすべてキャンセルする。
	 */
	public void cancelSubstitution() {
		for (Variable tVariable : mPool.values()) {
			tVariable.cancel();
		}
	}
	/**
	 * 代入をすべて確定する。
	 */
	public void settleSubstitution() {
		for (Variable tVariable : mPool.values()) {
			tVariable.settle();
		}
	}
	/**
	 * 一度以上使用することにより、現在登録された{@link Variable}を添え字の順番で一覧を表示する。
	 * @param aContext 表示するメッセージを取得するための{@link Context}。Localizationのために必要。
	 * @return mPoolの中身を文字列にしたもの。
	 */
	public String variablesToString(final Context aContext) {
		StringBuilder tBuffer = new StringBuilder();
		tBuffer.append(mPool.size() + aContext.getString(R.string.the_number_of_variables) + NEW_LINE);
		for (Map.Entry<String, Variable> tEntry : mPool.entrySet()) {
			tBuffer.append(tEntry.getKey());
			tBuffer.append(" : ");
			tBuffer.append(tEntry.getValue());
			tBuffer.append(NEW_LINE);
		}
		return tBuffer.toString();
	}
}
