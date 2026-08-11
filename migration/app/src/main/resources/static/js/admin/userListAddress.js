function openNewWindow(id) {
    const width = 350;
    const height = 300;
    const left = (screen.width - width) / 2;
    const top = (screen.height - height) / 2;
    const features = `width=${width},height=${height},left=${left},top=${top}`;

    window.open(`userAddress.jsp?id=${id}`, '_blank', features);
}