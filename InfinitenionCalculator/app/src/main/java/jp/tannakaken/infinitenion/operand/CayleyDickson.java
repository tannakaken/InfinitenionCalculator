package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;
/**
 * Cayley-Dicksonの構成法により、無限次元の超複素数を実装するクラス。<br>
 * 実部と虚部が可能無限的に続く二分木構造を実装し、枝であるこのクラスと、葉である{@link Rational}をスーパークラス{@link Constant}として同一視するために、
 * Compositeパターンを使用している。<br>
 * 詳しくは、package-infoを参照。<br>
 * 本来のJavaの思想から言えば、このクラスはimmutableにするべきだが、Android上で動かすためには、
 * 短命なオブジェクトをたくさん作ると、メモリが足らなくなり、メモリ確保のためにdarvikのガベージコレクタがGC_FOR_ALLOCを大量発生させ、
 * アプリが止まってしまう。<br>
 * 実際、これをimmutableにした設計でも、デスクトップアプリなら、十分なスピードで動くが、androidだと、E11111111の10乗程度でも止まってしまう。<br>
 * よって、このクラスはmutableにしてあり、演算をすると、operandに破壊的な副作用が起こる。<br>
 * どのoperandに破壊的副作用が起こるかはわからないので、演算に使ったObjectは再利用してはいけない。<br>
 * もう一度、使用しなくてはいけないときは、{@link Constant#copy()}を行う。
 * 
 * @author tannakaken
 *
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Cayley%E2%80%93Dickson_construction">
 * http://en.wikipedia.org/wiki/Cayley%E2%80%93Dickson_construction</a><br>
 * <a href="http://en.wikipedia.org/wiki/Composite_pattern">http://en.wikipedia.org/wiki/Composite_pattern</a>
 *
 */
class CayleyDickson extends Constant {

	/**
	 * 実部。省メモリ化のためにfinalにはしない。
	 */
	private final Constant mReal;
	/**
	 * 虚部。省メモリ化のためにfinalにはしない。
	 */
	private final Constant mImag;
	/**
	 * 高さ。これがintの限界2147483647を超えることはないと信じる理由がある。<br>
	 * このような高さを持つ虚数単位の添え字は2^2147483647であり、そのような数字を打ち込むには宇宙の寿命でもまだ足りないからである。
	 */
	private final int mHeight;
	/**
	 * このクラスを{@link Object#toString}するためのクラス。Visitorパターンを使用。<br>
	 * しかもこれはSingletonなので、他の場所でこのインスタンスに{@link ConstantStringConverter#setLimited(boolean)}すると、
	 * このインスタンスにも影響する。
	 * 
	 * @see 
	 * <a href="http://en.wikipedia.org/wiki/Visitor_pattern">http://en.wikipedia.org/wiki/Visitor_pattern</a><br>
	 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
	 * 
	 */
	private ConstantStringConverter mStringConverter = ConstantStringConverter.getInstance();
	
	/**
	 * 
	 * @param aReal 実部。
	 * @param aImag 虚部。
	 * @param aHeight 高さ。
	 */
	CayleyDickson(final Constant aReal, final Constant aImag, final int aHeight) {
		if (aReal == null) {
			throw new IllegalArgumentException("実部がnullではいけません。");
		}
		if (aImag == null) {
			throw new IllegalArgumentException("虚部がnullではいけません。");
		}
		if (aReal.getHeight() >= aHeight || aImag.getHeight() >= aHeight) {
			throw new IllegalArgumentException("実部と虚部の高さは、その数の高さより低くないといけません。");
		}
		mReal = aReal;
		mImag = aImag;
		mHeight = aHeight;
	}

	@Override
	public final Constant add(final Operand aOperand) {
		throw new UnsupportedOperationException("インスタンスメソッドのaddは高さ0の元にのみ実装されます。");
	}

	@Override
	public final Constant mul(final Operand aOperand) {
		throw new UnsupportedOperationException("インスタンスメソッドのmulは高さ0の元にのみ実装されます。");
	}

	@Override
	public final Constant negate() {
		throw new UnsupportedOperationException("インスタンスメソッドのnegateは高さ0の元にのみ実装されます。");
	}

	@Override
	public final Constant div(final Operand aOperand) {
		throw new UnsupportedOperationException("インスタンスメソッドのdivは高さ0の元にのみ実装されます。");
	}
	
	@Override
	public final Constant inv() {
		throw new UnsupportedOperationException("インスタンスメソッドのinvは高さ0の元にのみ実装されます。");
	}

	@Override
	public final boolean isZero() {
		return mReal.isZero() && mImag.isZero();
	}

	@Override
	final boolean isOne() {
		return mReal.isOne() && mImag.isZero(); 
	}
	

	@Override
	public final int getHeight() {
		return mHeight;
	}
	@Override
	public final Constant getReal() {
		return mReal;
	}
	@Override
	public final Constant getImag() {
		return mImag;
	}

	@Override
	final Constant drop() {
		if (mImag.isZero()) {
			return mReal;
		} else {
			return this;
		}
	}

	@Override
	public final String toString() {
		return mStringConverter.convertString(this);
	}

	@Override
	public final boolean isInteger() {
		return false;
	}

	@Override
	public final BigInteger getInteger() {
		throw new UnsupportedOperationException("このインスタンスは整数ではありません。");
	}
	
	@Override
	public final boolean equals(final Object aOther) {
		if (this == aOther) {
			return true;
		}
		if (!(aOther instanceof CayleyDickson)) {
			return false;
		}
		CayleyDickson tOtherCayleyDickson = (CayleyDickson) aOther;
		return this.mHeight == tOtherCayleyDickson.mHeight
			&& this.mReal.equals(tOtherCayleyDickson.mReal)
			&& this.mImag.equals(tOtherCayleyDickson.mImag);
	}
	@Override
	public final int hashCode() {
		return mReal.hashCode() + mImag.hashCode() + mHeight;
	}

}
