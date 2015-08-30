package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import android.content.Context;
import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.CalculatingException;

/**
 * 定数を現す抽象クラス。<br>
 * このクラスは、有理数を表す{@link Rational}と、
 * Cayley-Dicksonの構成法により構成された多元環の帰納極限である有理数上の無限次元代数を表す{@link CayleyDickson}の二つの具象サブクラスを持つ。<br>
 * これらは、実部と虚部の二つの枝を持つCayleyDicksonと実部しか持たない葉であるRationalによる二分木の構造を持っている。<br>
 * javaで木構造を定義する定石により、Compositeパターンを使用している。
 * またこのクラスは{@link Operand}の実装でもある。。<br>
 * また、Cayley-Dicksonの構成法に必要なメソッド、
 * および{@link ConstantStringConverter}による{@link Object#toString}に必要なメソッドも全て抽象メソッドとして用意されている。<br>
 * また{@link Constant#drop()}により、いつでも、無駄なメモリのない最適な状態に保たれている。<br>
 * なぜなら{@link ImaginaryFactory}で生成されたときは最適で、{@link Constant#calc(Operand[], String)}の計算の最後の{@link Constant#drop()}
 * により、最適化されているからである。<br>
 * これを実装したクラスは、{@link Object#equals}を適切にoverrideするべきである。
 * @author tannakaken
 *
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Template_method_pattern">
 * http://en.wikipedia.org/wiki/Template_method_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Composite_pattern">http://en.wikipedia.org/wiki/Composite_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Cayley%E2%80%93Dickson_construction">
 * http://en.wikipedia.org/wiki/Cayley%E2%80%93Dickson_construction</a>
 */
abstract class Constant implements Operand {

	/**
	 * {@link ImaginaryFactory}や{@link ConstantStringConverter}で、<br>
	 * {@link CayleyDickson}を構成したり、分解したりするときに使う定数。
	 */
	static final BigInteger TWO = BigInteger.valueOf(2);

	/**
	 * 
	 * @return 積の単位元かどうか。
	 */
	abstract boolean isOne();
	
	/**
	 * 値が0である枝を出来るだけ落としたもの。<br>
	 * メモリーの節約のため。
	 * @return 無駄な部分を落としたもの。
	 */
	abstract Constant drop();
	
	
	@Override
	public final void setInterior(final Constant aInterior) throws CalculatingException {
		throw new CalculatingException(R.string.substitution_not_variable_error);
	}
	@Override
	public final Constant getInterior() {
		return this;
	}
	/**
	 * @return {@link Constant}を文字列にしたもの。<br/>
	 * {@link Rational}ならば、逆ポーランド式分数表示「a b /」<br/>
	 * {@link Real}ならば、小数表記<br/>
	 * 高次元の{@link CayleyDickson}ならば、基底クラスに虚数単位Enを掛けたものの一次結合を、
	 * 逆ポーランド式に表示したものである。
	 */
	@Override
	public abstract String toString();
	
	/**
	 * 実数精度の設定を取り出すための{@link Context}。
	 */
	private static Context mContext;
	/** 
	 * @param aContext 現在の{@link Context}。
	 */
	static void setContext(final Context aContext) {
		mContext = aContext;
	}
	/**
	 * 
	 * @return 現在の{@link Context}
	 */
	static Context getContext() {
		return mContext;
	}
}
