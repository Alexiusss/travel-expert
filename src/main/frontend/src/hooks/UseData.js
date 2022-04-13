import {useMemo, useState} from "react";

//https://www.smashingmagazine.com/2020/03/sortable-tables-react/
export const useSortableData = (items, config) => {
    const [sortConfig, setSortConfig] = useState(config);

    const sortedItems = useMemo(() => {
        let sortableItems = [...items];
        if (sortConfig !== null) {
            sortableItems.sort((a, b) => {
                if (a[sortConfig.key] < b[sortConfig.key]) {
                    return sortConfig.direction === 'ascending' ? -1 : 1;
                }
                if (a[sortConfig.key] > b[sortConfig.key]) {
                    return sortConfig.direction === 'ascending' ? 1 : -1;
                }
                return 0;
            });
        }
        return sortableItems;
    }, [items, sortConfig])

    const requestSort = key => {
        let direction = 'ascending';
        if (sortConfig && sortConfig.key === key && sortConfig.direction === 'ascending') {
            direction = 'descending';
        }
        setSortConfig({key, direction});
    }

    return {items: sortedItems, requestSort, sortConfig};
};

export const useItems = (items, config, query, searchedColumns) => {

    const {items: sortedItems, requestSort, sortConfig} = useSortableData(items, config);

    // https://www.cluemediator.com/search-filter-for-multiple-object-in-reactjs
    const sortedAndSearchedItems = useMemo(() => {
        if (query === '') return sortedItems;

        const lowerCasedQuery = query.toLowerCase().trim();
        return sortedItems.filter(user => {
            return Object.keys(user).some(key =>
                searchedColumns.includes(key) ? user[key].toString().toLowerCase().includes(lowerCasedQuery) : false
            )
        });
    }, [query, sortedItems]);

    return {items: sortedAndSearchedItems, requestSort, sortConfig};

}