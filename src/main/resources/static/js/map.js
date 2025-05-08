const max_lon = 32.7769;
const min_lon = 23.1783;
const max_lat = 56.171945923;
const min_lat = 51.261945955;
const lon_differ = max_lon - min_lon;
const lat_differ = max_lat - min_lat;

window.onload = function() {
    const canvas = document.getElementById('mapCanvas');
    const ctx = canvas.getContext('2d');
    const img = new Image();

    img.onload = function() {
        // Сохраняем оригинальные размеры изображения
        const imgWidth = img.naturalWidth;
        const imgHeight = img.naturalHeight;

        // Устанавливаем размеры canvas согласно изображению
        canvas.width = imgWidth;
        canvas.height = imgHeight;

        // Отрисовываем изображение
        ctx.drawImage(img, 0, 0, imgWidth, imgHeight);

        // Отрисовываем города
        window.cities.forEach(city => {
            // Правильный расчет координат
            const x = ((city.longitude - min_lon) / lon_differ) * imgWidth;
            function mercatorY(lat) {
                return Math.log(Math.tan(Math.PI/4 + lat * Math.PI/360)) * 180/Math.PI;
            }
            const y_norm = (mercatorY(max_lat) - mercatorY(city.latitude)) /
                (mercatorY(max_lat) - mercatorY(min_lat));
            const y = y_norm * imgHeight;
            /*
            const y = ((max_lat - city.latitude) / lat_differ) * imgHeight;
            */

            let radius = city.central ? 5 : 3;

            // Рисуем точку
            ctx.beginPath();
            ctx.arc(x, y, radius, 0, Math.PI * 2); // Исправлены параметры arc()
            ctx.fillStyle = city.color;
            ctx.fill();

            ctx.strokeStyle = 'black';
            ctx.lineWidth = 0.2;
            ctx.stroke();

/*            ctx.fillStyle = 'black';
            ctx.font = '14px Arial';
            ctx.fillText(city.name, x + 10, y - 10);*/

        });
    };

    img.onerror = function() {
        console.error('Ошибка загрузки изображения');
    };

    img.src = window.imagePath;

    // Обработка изменения размера окна
    window.addEventListener('resize', () => {
        const aspectRatio = img.naturalWidth / img.naturalHeight;
        const containerWidth = canvas.parentElement.offsetWidth;

        canvas.style.width = containerWidth + 'px';
        canvas.style.height = (containerWidth / aspectRatio) + 'px';
    });
};