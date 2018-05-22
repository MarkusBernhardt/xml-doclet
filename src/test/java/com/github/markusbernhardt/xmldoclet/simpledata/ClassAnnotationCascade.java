package com.github.markusbernhardt.xmldoclet.simpledata;

/**
 * ClassAnnotationCascade
 */
@AnnotationCascade(
		children= {
			@AnnotationCascadeChild(name="primitive",
					dummyData= {"A", "B", "C"}
					),
			@AnnotationCascadeChild(name="nested",
			subAnnotations= {
					@Annotation3(id=4),
					@Annotation3(id=5),
					@Annotation3(id=666)
					
			})
			}
		)
public class ClassAnnotationCascade implements Interface2 {
	
	public void test() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int method1() {
		return 0;
	}
}
