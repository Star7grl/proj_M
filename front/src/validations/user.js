import * as yup from 'yup';

export const registerUserSchema = yup.object({
    name:yup.string().required('Обязательно для заполнения')
});