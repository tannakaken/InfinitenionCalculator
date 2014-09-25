package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;
import java.util.regex.Pattern;

import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;

/**
 * トークンから超複素数の虚数単位を生成するFactory。<br>
 * 一つしか必要ないので、Singletonパターンを使う。<br>
 * 必ず{@link Factory#setContext(Context)}内で、{@link ImaginaryFactory#setContexttoConstant(Context)}をすること。<br>
 * それを怠った場合の挙動は保証しない。
 * 
 * @author tannakaken
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">
 * http://en.wikipedia.org/wiki/Factory_method_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 */
public final class ImaginaryFactory extends Factory {
	/**
	 * 実数以外のConstantをあらわす記号化どうかを判定する正規表現。
	 */
	private static String mRegex = "^E[1-9][0-9]*$";
	/**
	 * 正規表現をコンパイル。
	 */
	private static Pattern mPattern = Pattern.compile(mRegex);
	/**
	 * 1を生成するためのFactory。
	 */
	private static BaseFieldFactory mRealFactory = BaseFieldFactory.getInstance();
	/**
	 * {@link ImaginaryFactroy}の<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>。
	 * 
	 */
	private static ImaginaryFactory mSingleton = new ImaginaryFactory();
	/**
	 * {@link CayleyDickson}を作るための、受け入れ可能なトークン。
	 */
	private String mToken;
	/**
	 * 外からインスタンス化させない。
	 */
	private ImaginaryFactory() { }
	/**
	 * 
	 * @return {@link ImaginaryFactory}の<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>
	 * 
	 */
	public static ImaginaryFactory getInstance() {
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
	public void calc() throws BackgroundProcessCancelledException {
		getStack().push(buildConstant(number(mToken)));
	}
	/**
	 * 
	 * @param aToken {@link Constant}と解釈されるトークン。
	 * @return そのトークンが表している虚数単位の番号。
	 */
	private BigInteger number(final String aToken) {
		return new BigInteger(aToken.substring(1));
	}
	/**
	 * 虚数単位の番号から、木構造を計算し、その虚数単位を表す{@link CayleyDickson}のインスタンスを返す。
	 * @param aNumber 虚数単位の番号。
	 * @return aNumber番目の虚数単位。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。 
	 */
	private Constant buildConstant(final BigInteger aNumber) throws BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		// 目的の枝にたどり着いたら、係数に1を代入。
		if (aNumber.equals(BigInteger.ZERO)) {
			return mRealFactory.getOne();
		// 目的の枝にたどり着くまで、aNumberを二進展開していく。
		} else {
			int tHeight = calculateHeight(aNumber);
			return new CayleyDickson(Zero.ZERO,
									 buildConstant(aNumber.subtract(Constant.TWO.pow(tHeight - 1))),
									 tHeight);
		}
	}
	/**
	 * 
	 * @param aNumber 虚数単位の番号。
	 * @return その虚数単位が属する2^n元数のn
	 */
	private int calculateHeight(final BigInteger aNumber) {
		int tHeight = 0;
		BigInteger tNum = BigInteger.ONE;
		while (tNum.compareTo(aNumber) <= 0) {
			tHeight++;
			tNum = tNum.multiply(Constant.TWO);
		}
		return tHeight;
	}
	/**
	 * {@link CayleyDickson}のインスタンスを再利用することになったときのために、このメソッドを用意している。
	 * @param aReal 実成分
	 * @param aImag 虚数成分
	 * @param aHeight 高さ
	 * @return 構成され、最適化された{@link Constant}
	 */
	Constant mixRealAndImaginary(final Constant aReal, final Constant aImag, final int aHeight) {
		return (new CayleyDickson(aReal, aImag, aHeight)).drop();
	}
}
