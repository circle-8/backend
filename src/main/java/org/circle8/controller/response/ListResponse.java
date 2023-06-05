package org.circle8.response;

import java.util.List;

public class ListResponse<T> implements ApiResponse {
	/** Pagina actual (indice 0)*/
	public int pageNumber;
	/** Cantidad total de paginas */
	public int totalPages;
	/** Size de la pagina actual */
	public int pageSize;
	/** Cantidad total de elementos (sin paginar) */
	public int count;
	/** Pagina siguiente, si hay */
	public String nextPage;
	/** Pagina anterior, si hay */
	public String prevPage;
	/** Datos a enviar */
	public List<T> data;

	public ListResponse(
		int pageNumber,
		int total,
		int count,
		String next,
		String prev,
		List<T> data
	) {
		this.pageNumber = pageNumber;
		this.totalPages = total;
		this.count = count;
		this.nextPage = next;
		this.prevPage = prev;
		this.data = data;
		this.pageSize = this.data.size();
	}
}
