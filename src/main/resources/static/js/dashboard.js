(() => {
    const searchInput = document.getElementById('developer-search');
    const levelFilter = document.getElementById('level-filter');
    const teamFilter = document.getElementById('team-filter');
    const statusFilter = document.getElementById('status-filter');
    const rows = Array.from(document.querySelectorAll('.developer-row'));
    const emptyState = document.getElementById('empty-state');
    const visibleCount = document.getElementById('visible-count');

    if (!rows.length || !searchInput) return;

    const filterRows = () => {
        const search = searchInput.value.trim().toLowerCase();
        const level = levelFilter.value;
        const team = teamFilter.value;
        const status = statusFilter.value;
        let visible = 0;

        rows.forEach((row) => {
            const matches = (!search || row.dataset.search.includes(search))
                && (!level || row.dataset.level === level)
                && (!team || row.dataset.team === team)
                && (!status || row.dataset.status === status);
            row.hidden = !matches;
            if (matches) visible++;
        });

        emptyState.hidden = visible !== 0;
        visibleCount.textContent = `Showing ${visible} of ${rows.length} developers`;
    };

    [searchInput, levelFilter, teamFilter, statusFilter].forEach((control) => {
        control.addEventListener(control === searchInput ? 'input' : 'change', filterRows);
    });

    const dateLabel = document.getElementById('today-label');
    if (dateLabel) {
        dateLabel.textContent = new Intl.DateTimeFormat(undefined, {
            weekday: 'short', month: 'short', day: 'numeric', year: 'numeric'
        }).format(new Date()).toUpperCase();
    }
})();
