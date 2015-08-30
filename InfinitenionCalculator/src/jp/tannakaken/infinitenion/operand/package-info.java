/**
 * 実際に数の生成や、数同士の演算を行っているパッケージ。<br>
 * MVCでいうモデル。<br>
 * 演算の具体的な内容はこのパッケージが責任を持つ。<br>
 * {@link Zero}はゼロ元を意味するSingleton。<br>
 * {@link Ratinal}と{@link Real}が、基礎体としての役割を持ち、{@link BaseFieldFactory}がトークンをパースして生成する。<br>
 * {@link CaykleyDickson}がCayley-Dicksonの構成法を実装しており、{@link ImaginaryFactory}ががトークンをパースして生成する。<br>
 * {@link Variable}が中身の可変な変数を表し、{@link VariableFactory}がトークンをパースして生成したり、プールされたものを取り出したりする。<br>
 * ２次元以上の数については、{@link ResultantFactory}が演算し、１次元、つまり基礎体に属する数は、インスタンス自信に計算機能が付属している。<br>
 * {@link ConstantStringConverter}が計算結果を文字列に変換し、{@link Calculator}に送り返す。
 * 
 * @author tannakaken
 * @see
 * <a href="http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">
 * http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller</a>
 *
 */
package jp.tannakaken.infinitenion.operand;