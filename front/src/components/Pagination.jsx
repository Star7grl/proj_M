import React from 'react';

const Pagination = ({ totalItems, itemsPerPage, currentPage, onPageChange }) => {
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

  return (
    <div className="pagination">
      {pages.map((page) => (
        <button
        key={page}
        type="button"  // Добавлено
        onClick={() => onPageChange(page)}
        className={page === currentPage ? 'active' : ''}
      >
        {page}
      </button>
      ))}
    </div>
  );
};

export default Pagination;